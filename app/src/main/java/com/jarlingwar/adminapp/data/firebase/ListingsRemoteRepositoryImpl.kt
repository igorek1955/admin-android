package com.jarlingwar.adminapp.data.firebase

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.domain.models.ListingStatus
import com.jarlingwar.adminapp.domain.models.ListingsQueryParams
import com.jarlingwar.adminapp.domain.models.SortOrder
import com.jarlingwar.adminapp.domain.repositories.remote.DeleteListingResponse
import com.jarlingwar.adminapp.domain.repositories.remote.IListingsRemoteRepository
import com.jarlingwar.adminapp.domain.repositories.remote.SaveListingResponse
import com.jarlingwar.adminapp.utils.FirestoreCollections
import com.jarlingwar.adminapp.utils.ListingFields
import com.jarlingwar.adminapp.utils.RemoteConfig
import com.jarlingwar.adminapp.utils.geo.CountryInfo
import com.jarlingwar.adminapp.utils.geo.capitalized
import com.jarlingwar.adminapp.utils.paginate
import com.jarlingwar.adminapp.utils.toUnknown
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ListingsRemoteRepositoryImpl(private val remoteConfig: RemoteConfig, db: FirebaseFirestore) :
    IListingsRemoteRepository {
    private var listings = db.collection(FirestoreCollections.LISTINGS)
    private var params: ListingsQueryParams = ListingsQueryParams()
    private val preferredOrder: SortOrder get() = params.orderBy!!

    override fun updateParams(queryParams: ListingsQueryParams) {
        params.update(queryParams)
    }
    override fun getParams() = params

    override suspend fun saveListing(listing: ListingModel): SaveListingResponse {
        return try {
            listings
                .document(listing.listingId)
                .set(listing)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteListing(listing: ListingModel): DeleteListingResponse {
        return try {
            listings
                .document(listing.listingId)
                .delete()
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * used for searching and recommendations on main screen
     */
    override suspend fun getListingsByTags(
        tagList: List<String>,
        queryLimit: Long
    ): Result<List<ListingModel>> {
        val query = listings.whereArrayContainsAny(ListingFields.TAGS, tagList)
        return processQuery(query, primaryOrder = SortOrder.PRICE_ASC)
    }

    /**
     * Query. 'in' filters support a maximum of 10 elements in the value array.
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun getListings(idList: List<String>): Result<List<ListingModel>> {
        if (idList.size >= 10) {
            return suspendCoroutine { continuation ->
                val batches = idList.chunked(10)
                val tasks: ArrayList<Task<QuerySnapshot>> = arrayListOf()
                val results = arrayListOf<ListingModel>()
                for (b in batches) {
                    val query = listings.whereIn(ListingFields.ID, b)
                    tasks.add(query.get())
                }
                Tasks.whenAllComplete(tasks)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            it.result.forEach { t ->
                                val task = t as Task<QuerySnapshot>
                                val listings = arrayListOf<ListingModel>()
                                task.result.documents.forEach { snapshot ->
                                    val listingModel: ListingModel? =
                                        snapshot.toObject(ListingModel::class.java)
                                    listingModel?.let { listings.add(it) }
                                }
                                results.addAll(listings)
                            }
                            continuation.resume(Result.success(results))
                        } else continuation.resume(Result.failure(it.exception.toUnknown()))
                    }
            }
        }
        val query = listings.whereIn(ListingFields.ID, idList)
        return processQuery(query, primaryOrder = SortOrder.CREATED_DESC, ignoreSecondaryOrder = true)
    }

    override suspend fun getListingsByUserId(userId: String): Result<List<ListingModel>> {
        val query = listings.whereEqualTo(ListingFields.USER_ID, userId)
        return processQuery(query, primaryOrder = SortOrder.CREATED_DESC, ignoreSecondaryOrder = true)
    }

    override fun getPublishedListingsPaging(pagingReference: Flow<Int>): Flow<List<ListingModel?>> {
        var query = listings
            .whereEqualTo(ListingFields.APPROVED, true)
            .whereEqualTo(ListingFields.STATUS, ListingStatus.PUBLISHED)
        if (params.country != CountryInfo.INTERNATIONAL) {
            query = query.whereEqualTo(ListingFields.COUNTRY, params.country?.name?.capitalized())
        }
        return processQueryFlow(query, pagingReference, SortOrder.CREATED_DESC)
    }

    override fun getPendingListingsPaging(pagingReference: Flow<Int>): Flow<List<ListingModel?>> {
        var query = listings
            .whereEqualTo(ListingFields.APPROVED, false)
            .whereEqualTo(ListingFields.STATUS, ListingStatus.PUBLISHED)
        if (params.country != CountryInfo.INTERNATIONAL) {
            query = query.whereEqualTo(ListingFields.COUNTRY, params.country?.name?.capitalized())
        }
        return processQueryFlow(query, pagingReference, SortOrder.CREATED_DESC)
    }

    private fun processQueryFlow(
        query: Query,
        pagingStartVal: Flow<Int>,
        primaryOrder: SortOrder? = null
    ):Flow<List<ListingModel?>> {
        val orderedQuery = getOrderedQuery(query, primaryOrder, false)
        return orderedQuery
            .paginate(pagingStartVal, remoteConfig.paginationLimit)
            .map { docs -> docs.map { it.toObject(ListingModel::class.java) } }
    }

    private fun getOrderedQuery(query: Query, primaryOrder: SortOrder?, ignoreSecondaryOrder: Boolean): Query {
        var orderedQuery = query
        if (ignoreSecondaryOrder) {
            if (primaryOrder != null) orderedQuery = query.orderBy(primaryOrder.fieldName, primaryOrder.direction)
        } else {
            val secondaryOrder = preferredOrder
            orderedQuery = if (primaryOrder?.fieldName == secondaryOrder.fieldName) {
                query.orderBy(secondaryOrder.fieldName, secondaryOrder.direction)
            } else if (primaryOrder != null) {
                query
                    .orderBy(primaryOrder.fieldName, primaryOrder.direction)
                    .orderBy(secondaryOrder.fieldName, secondaryOrder.direction)
            } else {
                query.orderBy(secondaryOrder.fieldName, secondaryOrder.direction)
            }
        }
        return orderedQuery
    }

    private suspend fun processQuery(
        query: Query,
        primaryOrder: SortOrder? = null,
        ignoreSecondaryOrder: Boolean = false
    ): Result<List<ListingModel>> =
        suspendCoroutine { continuation ->
            val orderedQuery = getOrderedQuery(query, primaryOrder, ignoreSecondaryOrder)
            orderedQuery
                .get()
                .addOnSuccessListener { documentSnapshots ->
                    val listings = arrayListOf<ListingModel>()
                    documentSnapshots.documents.forEach { snapshot ->
                        val listingModel: ListingModel? =
                            snapshot.toObject(ListingModel::class.java)
                        listingModel?.let { listings.add(it) }
                    }
                    continuation.resume(Result.success(listings))
                }
                .addOnFailureListener { continuation.resume(Result.failure(it)) }
        }
}
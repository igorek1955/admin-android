package com.jarlingwar.adminapp.domain

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.domain.models.ListingsQueryParams
import com.jarlingwar.adminapp.domain.models.SortOrder
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.domain.repositories.remote.DeleteListingResponse
import com.jarlingwar.adminapp.domain.repositories.remote.IListingsRepository
import com.jarlingwar.adminapp.domain.repositories.remote.SaveListingResponse
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.FirestoreCollections
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.toUnknown
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface IListingManager {
    suspend fun saveListing(listingModel: ListingModel): SaveListingResponse
    suspend fun saveListings(listings: List<ListingModel>): SaveListingResponse
    suspend fun deleteListings(listings: List<ListingModel>, deleteImages: Boolean = true): DeleteListingResponse
    suspend fun deleteListing(listingModel: ListingModel, deleteImages: Boolean = true): DeleteListingResponse
    suspend fun getListings(idList: List<String>): Result<List<ListingModel>>
    suspend fun searchListings(query: String, queryLimit: Long = 50): Result<List<ListingModel>>
    suspend fun getUserListings(user : UserModel): Result<List<ListingModel>>
    suspend fun getUserListings(userId : String): Result<List<ListingModel>>
    suspend fun deleteUserImages(userId: String): Result<Boolean>
    suspend fun getPubListingsByDate(updateTime: Long): Result<List<ListingModel>>
    fun getPublishedListingsPaging(pagingReference: Flow<Int>) : Flow<List<ListingModel?>>
    fun getPendingListingsPaging(pagingReference: Flow<Int>) : Flow<List<ListingModel?>>
    fun updateParams(params: ListingsQueryParams? = null, order: SortOrder? = null)
    fun getParams(): ListingsQueryParams
}

@ViewModelScoped
class ListingManager @Inject constructor(
    private val remoteStorage: IListingsRepository
) : IListingManager {
    private val imageStorage = FirebaseStorage.getInstance().reference


    override suspend fun saveListing(listingModel: ListingModel): SaveListingResponse {
        listingModel.updated = System.currentTimeMillis()
        return remoteStorage.saveListing(listingModel)
    }

    override suspend fun saveListings(listings: List<ListingModel>): SaveListingResponse {
        return withContext(Dispatchers.IO) {
            try {
                remoteStorage.saveListings(listings)
            } catch (e: Exception) {
                ReportHandler.reportError(e)
                Result.failure(e)
            }
        }
    }


    override suspend fun deleteListings(
        listings: List<ListingModel>,
        deleteImages: Boolean
    ): DeleteListingResponse {
        return withContext(Dispatchers.IO) {
            try {
                var exception: Throwable? = null
                listings.forEach { listing ->
                    if (exception == null) {
                        exception = deleteListing(listing, deleteImages).exceptionOrNull()
                    }
                }
                if (listings.isEmpty()) {
                    Result.success(true)
                } else if (exception != null) {
                    Result.failure(exception.toUnknown())
                } else {
                    Result.success(true)
                }
            } catch (e: Exception) {
                ReportHandler.reportError(e)
                Result.failure(e.toUnknown())
            }
        }
    }

    override suspend fun deleteListing(
        listingModel: ListingModel,
        deleteImages: Boolean
    ): DeleteListingResponse {
        return withContext(Dispatchers.IO) {
            if (deleteImages) deleteImages(listingModel.remoteImgUrlList)
            remoteStorage.deleteListing(listingModel)
        }
    }

    override suspend fun getListings(idList: List<String>) : Result<List<ListingModel>> {
        return withContext(Dispatchers.IO) {
            try {
                remoteStorage.getListings(idList)
            } catch (e : Exception) {
                ReportHandler.reportError(e, "$idList")
                Result.failure(e)
            }
        }
    }

    override suspend fun searchListings(query: String, queryLimit: Long) =
        withContext(Dispatchers.IO) {
            try {
                if (query.isEmpty()) {
                    Result.failure(CustomError.GeneralError.NoResults())
                } else {
                    val queries = query.split(" ", ",")
                    remoteStorage.getListingsByTags(queries, queryLimit)
                }
            } catch (e: Exception) {
                ReportHandler.reportError(e)
                Result.failure(e)
            }
        }

    override suspend fun getUserListings(user: UserModel): Result<List<ListingModel>> {
        return withContext(Dispatchers.IO) {
            try {
                remoteStorage.getListingsByUserId(user.userId)
            } catch (e : Exception) {
                ReportHandler.reportError(e, "$user")
                Result.failure(e)
            }
        }
    }

    override suspend fun getUserListings(userId: String): Result<List<ListingModel>> {
        return withContext(Dispatchers.IO) {
            try {
                remoteStorage.getListingsByUserId(userId)
            } catch (e : Exception) {
                ReportHandler.reportError(e, userId)
                Result.failure(e)
            }
        }
    }

    override fun getPublishedListingsPaging(pagingReference: Flow<Int>)
        = remoteStorage.getPublishedListingsPaging(pagingReference)

    override fun getPendingListingsPaging(pagingReference: Flow<Int>)
        = remoteStorage.getPendingListingsPaging(pagingReference)


    override fun updateParams(params: ListingsQueryParams?, order: SortOrder?) {
        val updatedParams = params ?: ListingsQueryParams()
        order?.let { updatedParams.orderBy = order }
        params?.let { remoteStorage.updateParams(params) }
        remoteStorage.updateParams(updatedParams)
    }

    override fun getParams() = remoteStorage.getParams()

    override suspend fun deleteUserImages(userId: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { c ->
                try {
                    imageStorage.child("${FirestoreCollections.IMAGES}/$userId/").listAll()
                        .addOnSuccessListener { images ->
                            val tasks: ArrayList<Task<Void>> = arrayListOf()
                            for (i in images.items) {
                                tasks.add(i.delete())
                            }
                            Tasks.whenAll(tasks)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) c.resume(Result.success(true))
                                    else c.resume(Result.failure(it.exception.toUnknown()))
                                }
                        }
                        .addOnFailureListener {
                            c.resume(Result.failure(it.toUnknown()))
                        }
                } catch (e: Exception) {
                    ReportHandler.logEvent(e)
                    c.resume(Result.failure(e))
                }
            }
        }
    }

    override suspend fun getPubListingsByDate(updateTime: Long): Result<List<ListingModel>> {
        return withContext(Dispatchers.IO) {
            try {
                remoteStorage.getPubListingsByDate(updateTime)
            } catch (e: Exception) {
                ReportHandler.reportError(e)
                Result.failure(e)
            }
        }
    }

    private fun deleteImages(photos: List<String>) {
        try {
            val tasks: ArrayList<Task<Void>> = arrayListOf()
            for (p in photos) {
                val lastSegment = Uri.parse(p).lastPathSegment
                if (!lastSegment.isNullOrEmpty()) {
                    tasks.add(imageStorage.child(lastSegment).delete())
                }
            }
        } catch (e: Exception) {
            ReportHandler.logEvent(e)
        }
    }
}
package com.jarlingwar.adminapp.domain

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.domain.models.ListingsQueryParams
import com.jarlingwar.adminapp.domain.models.SortOrder
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.domain.repositories.remote.DeleteListingResponse
import com.jarlingwar.adminapp.domain.repositories.remote.IListingsRepository
import com.jarlingwar.adminapp.domain.repositories.remote.SaveListingResponse
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.toUnknown
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface IListingManager {
    suspend fun saveListing(listingModel: ListingModel): SaveListingResponse
    suspend fun deleteListings(idList: List<String>): DeleteListingResponse
    suspend fun deleteListing(listingModel: ListingModel): DeleteListingResponse
    suspend fun getListings(idList: List<String>): Result<List<ListingModel>>
    suspend fun searchListings(query: String, queryLimit: Long = 50): Result<List<ListingModel>>
    suspend fun getUserListings(user : UserModel): Result<List<ListingModel>>
    suspend fun getUserListings(userId : String): Result<List<ListingModel>>
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

    override suspend fun deleteListings(idList: List<String>): DeleteListingResponse {
        return withContext(Dispatchers.IO) {
            try {
                val listings = getListings(idList).getOrNull()
                var exception: Throwable? = null
                listings?.forEach { listing ->
                    if (exception == null) {
                        exception = deleteListing(listing).exceptionOrNull()
                    }
                }
                if (listings.isNullOrEmpty() || exception != null) {
                    Result.failure(exception.toUnknown())
                } else Result.success(true)
            } catch (e: Exception) {
                Result.failure(e.toUnknown())
            }
        }
    }

    override suspend fun deleteListing(listingModel: ListingModel): DeleteListingResponse {
        return withContext(Dispatchers.IO) {
            remoteStorage.deleteListing(listingModel)
                .onSuccess { deletePhotos(listingModel.remoteImgUrlList) }
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

    private fun deletePhotos(photos: List<String>) {
        try {
            val tasks: ArrayList<Task<Void>> = arrayListOf()
            for (p in photos) {
                val lastSegment = Uri.parse(p).lastPathSegment
                if (!lastSegment.isNullOrEmpty()) {
                    tasks.add(imageStorage.child(lastSegment).delete())
                }
            }
        } catch (e: Exception) {
            ReportHandler.reportError(e)
        }
    }
}
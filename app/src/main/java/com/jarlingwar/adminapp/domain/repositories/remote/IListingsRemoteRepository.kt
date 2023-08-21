package com.jarlingwar.adminapp.domain.repositories.remote

import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.domain.models.QueryParams
import kotlinx.coroutines.flow.Flow

typealias SaveListingResponse = Result<Boolean>
typealias DeleteListingResponse = Result<Boolean>

interface IListingsRemoteRepository {
    fun updateParams(queryParams: QueryParams)
    fun getParams(): QueryParams
    suspend fun saveListing(listing: ListingModel): SaveListingResponse
    suspend fun deleteListing(listing: ListingModel): DeleteListingResponse
    suspend fun getListingsByTags(tagList: List<String>, queryLimit: Long): Result<List<ListingModel>>
    suspend fun getListings(idList: List<String>): Result<List<ListingModel>>
    suspend fun getListingsByUserId(userId: String): Result<List<ListingModel>>
    fun getPublishedListingsPaging(pagingReference: Flow<Int>) : Flow<List<ListingModel?>>
    fun getPendingListingsPaging(pagingReference: Flow<Int>) : Flow<List<ListingModel?>>
}
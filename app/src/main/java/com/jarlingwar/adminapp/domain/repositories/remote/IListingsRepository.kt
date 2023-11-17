package com.jarlingwar.adminapp.domain.repositories.remote

import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.domain.models.ListingsQueryParams
import kotlinx.coroutines.flow.Flow

typealias SaveListingResponse = Result<Boolean>
typealias DeleteListingResponse = Result<Boolean>

interface IListingsRepository {
    fun updateParams(queryParams: ListingsQueryParams)
    fun getParams(): ListingsQueryParams
    suspend fun getPubListingsByDate(updateTime: Long): Result<List<ListingModel>>
    suspend fun saveListing(listing: ListingModel): SaveListingResponse
    suspend fun saveListings(listings: List<ListingModel>): SaveListingResponse
    suspend fun deleteListing(listing: ListingModel): DeleteListingResponse
    suspend fun getListingsByTags(tagList: List<String>, queryLimit: Long): Result<List<ListingModel>>
    suspend fun getListings(idList: List<String>): Result<List<ListingModel>>
    suspend fun getListingsByUserId(userId: String): Result<List<ListingModel>>
    fun getPublishedListingsPaging(pagingReference: Flow<Int>) : Flow<List<ListingModel?>>
    fun getPendingListingsPaging(pagingReference: Flow<Int>) : Flow<List<ListingModel?>>
}
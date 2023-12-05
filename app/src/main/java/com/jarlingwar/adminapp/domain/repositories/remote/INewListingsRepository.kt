package com.jarlingwar.adminapp.domain.repositories.remote

import com.jarlingwar.adminapp.domain.models.ListingModel
import kotlinx.coroutines.flow.Flow

interface INewListingsRepository {
    fun getNewListingsAsFlow(): Flow<List<ListingModel>>
    suspend fun approveListing(id: String): Result<Unit>
    suspend fun getListing(id: String): Result<ListingModel>
}
package com.jarlingwar.adminapp.domain.repositories.remote

import com.jarlingwar.adminapp.domain.models.ReviewModel

interface IReviewRepository {
    suspend fun getReviewsByUserId(userId: String): Result<List<ReviewModel>>
    suspend fun deleteReviews(idList: List<String>): Result<Unit>
    suspend fun deleteUserReviews(userId: String): Result<Unit>
    suspend fun getUserAuthoredReviews(userId: String): Result<List<ReviewModel>>
}
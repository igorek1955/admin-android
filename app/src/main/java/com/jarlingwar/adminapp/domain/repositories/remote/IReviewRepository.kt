package com.jarlingwar.adminapp.domain.repositories.remote

import com.jarlingwar.adminapp.domain.models.ReviewModel
import kotlinx.coroutines.flow.Flow

interface IReviewRepository {
    suspend fun approveReview(reviewId: String): Result<Unit>
    suspend fun deleteReview(reviewId: String): Result<Unit>
    suspend fun updateReview(reviewModel: ReviewModel): Result<Unit>
    suspend fun getReviewsByUserId(userId: String): Result<List<ReviewModel>>
    suspend fun deleteReviews(idList: List<String>): Result<Unit>
    suspend fun deleteUserReviews(userId: String): Result<Unit>
    suspend fun getUserAuthoredReviews(userId: String): Result<List<ReviewModel>>
    fun getReviewsAsFlow(): Flow<List<ReviewModel>>
    fun getReviewsPaging(pagingReference: Flow<Int>): Flow<List<ReviewModel>>
    fun getPendingReviewsPaging(pagingReference: Flow<Int>): Flow<List<ReviewModel>>
}
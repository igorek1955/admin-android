package com.jarlingwar.adminapp.data.firebase

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.WriteBatch
import com.jarlingwar.adminapp.domain.models.ReviewModel
import com.jarlingwar.adminapp.domain.repositories.remote.IReviewRepository
import com.jarlingwar.adminapp.utils.FirestoreCollections
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.ReviewFields
import com.jarlingwar.adminapp.utils.paginate
import com.jarlingwar.adminapp.utils.toUnknown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ReviewRepositoryImpl(private val db: FirebaseFirestore) : IReviewRepository {
    private val reviews = db.collection(FirestoreCollections.REVIEWS)
    override suspend fun updateReview(reviewModel: ReviewModel): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                reviews
                    .document(reviewModel.id)
                    .set(reviewModel)
                    .await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getReviewsByUserId(userId: String): Result<List<ReviewModel>> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                try {
                    reviews
                        .whereEqualTo(ReviewFields.USER_ID, userId)
                        .get()
                        .addOnSuccessListener { documentSnapshots ->
                            val reviews = arrayListOf<ReviewModel>()
                            documentSnapshots.documents.forEach { snapshot ->
                                val reviewModel: ReviewModel? =
                                    snapshot.toObject(ReviewModel::class.java)
                                reviewModel?.let { reviews.add(it) }
                            }
                            continuation.resume(Result.success(reviews))
                        }.addOnFailureListener { continuation.resume(Result.failure(it.toUnknown())) }
                } catch (e: Exception) {
                    ReportHandler.reportError(e)
                    continuation.resume(Result.failure(e.toUnknown()))
                }
            }
        }
    }



    override suspend fun deleteReviews(idList: List<String>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                try {
                    val tasks: ArrayList<Task<Void>> = arrayListOf()
                    idList.forEach {
                        tasks.add(reviews.document(it).delete())
                    }
                    Tasks.whenAll(tasks)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                continuation.resume(Result.success(Unit))
                            } else continuation.resume(Result.failure(it.exception.toUnknown()))
                        }
                } catch (e: Exception) {
                    continuation.resume(Result.failure(e.toUnknown()))
                }
            }
        }
    }

    override suspend fun deleteUserReviews(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val docs: ArrayList<DocumentSnapshot> = arrayListOf()
                val job1 = async {
                    reviews
                        .whereEqualTo(ReviewFields.USER_ID, userId)
                        .get()
                }
                val job2 = async {
                    reviews
                        .whereEqualTo(ReviewFields.USER_ID, userId)
                        .get()
                }
                job1.await().await()?.let { docs.addAll(it.documents) }
                job2.await().await()?.let { docs.addAll(it.documents) }
                if (docs.isNotEmpty()) {
                    suspendCoroutine { continuation ->
                        val writeBatch: WriteBatch = db.batch()
                        docs.forEach { doc -> writeBatch.delete(doc.reference) }
                        writeBatch.commit()
                            .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
                            .addOnFailureListener { continuation.resume(Result.failure(it.toUnknown())) }
                    }
                } else Result.success(Unit)
            } catch (e: Exception) {
                ReportHandler.reportError(e)
                Result.failure(e.toUnknown())
            }
        }
    }

    override suspend fun getUserAuthoredReviews(userId: String): Result<List<ReviewModel>> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                try {
                    reviews
                        .whereEqualTo(ReviewFields.REVIEWER_ID, userId)
                        .get()
                        .addOnSuccessListener { documentSnapshots ->
                            val reviews = arrayListOf<ReviewModel>()
                            documentSnapshots.documents.forEach { snapshot ->
                                val reviewModel: ReviewModel? =
                                    snapshot.toObject(ReviewModel::class.java)
                                reviewModel?.let { reviews.add(it) }
                            }
                            continuation.resume(Result.success(reviews))
                        }.addOnFailureListener { continuation.resume(Result.failure(it.toUnknown())) }
                } catch (e: Exception) {
                    ReportHandler.reportError(e)
                    continuation.resume(Result.failure(e.toUnknown()))
                }
            }
        }
    }

    override fun getReviewsPaging(pagingReference: Flow<Int>): Flow<List<ReviewModel>> {
        return reviews
            .orderBy(ReviewFields.CREATED, Query.Direction.DESCENDING)
            .paginate(pagingReference, 50)
            .map { docs -> docs.mapNotNull { it.toObject(ReviewModel::class.java) } }
    }

    override fun getPendingReviewsPaging(pagingReference: Flow<Int>): Flow<List<ReviewModel>> {
        return reviews
            .whereEqualTo(ReviewFields.APPROVED, false)
            .orderBy(ReviewFields.CREATED, Query.Direction.DESCENDING)
            .paginate(pagingReference, 50)
            .map { docs -> docs.mapNotNull { it.toObject(ReviewModel::class.java) } }
    }
}
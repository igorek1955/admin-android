package com.jarlingwar.adminapp.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.domain.models.ListingStatus
import com.jarlingwar.adminapp.domain.repositories.remote.INewListingsRepository
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.FirestoreCollections
import com.jarlingwar.adminapp.utils.ListingFields
import com.jarlingwar.adminapp.utils.ReportHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NewListingsRepositoryImpl(db: FirebaseFirestore) : INewListingsRepository {
    private val listings = db.collection(FirestoreCollections.LISTINGS)
    override fun getNewListingsAsFlow(): Flow<List<ListingModel>> {
        return listings
            .whereEqualTo(ListingFields.APPROVED, false)
            .whereEqualTo(ListingFields.STATUS, ListingStatus.PUBLISHED)
            .snapshots()
            .map {
                it.documents.mapNotNull { doc ->
                    doc.toObject(ListingModel::class.java)
                }
            }

    }

    override suspend fun approveListing(id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                suspendCoroutine { continuation ->
                    listings
                        .document(id)
                        .get()
                        .addOnCompleteListener { snapshot ->
                            if (snapshot.isSuccessful) {
                                val listing = snapshot.result.toObject(ListingModel::class.java)
                                if (listing != null) {
                                    listing.approved = true
                                    listings.document(id)
                                        .set(listing)
                                        .addOnCompleteListener {
                                            continuation.resume(Result.success(Unit))
                                        }
                                } else {
                                    continuation.resume(Result.failure(CustomError.ListingError.ListingNotFound()))
                                }
                            } else {
                                continuation.resume(Result.failure(snapshot.exception ?: Throwable()))
                            }
                        }
                }
            } catch (e: Exception) {
                ReportHandler.reportError(e)
                Result.failure(e)
            }
        }
    }
}
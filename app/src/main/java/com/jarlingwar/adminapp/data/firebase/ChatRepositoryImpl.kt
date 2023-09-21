package com.jarlingwar.adminapp.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.jarlingwar.adminapp.domain.models.ChannelModel
import com.jarlingwar.adminapp.domain.models.NotificationModel
import com.jarlingwar.adminapp.domain.repositories.remote.IChatRepository
import com.jarlingwar.adminapp.utils.ChatFields
import com.jarlingwar.adminapp.utils.FirestoreCollections
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.toUnknown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChatRepositoryImpl(db: FirebaseFirestore) : IChatRepository {
    private val channels = db.collection(FirestoreCollections.CHANNELS)
    private val notifications = db.collection(FirestoreCollections.NOTIFICATIONS)
    override fun getNotificationQueueAsFlow(): Flow<List<NotificationModel>> {
        return notifications
            .snapshots()
            .catch {  }
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(NotificationModel::class.java) }
            }
    }

    override suspend fun getChatChannels(userId: String): Result<Int> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                try {
                    channels
                        .whereEqualTo(ChatFields.USER_A, userId)
                        .get()
                        .addOnSuccessListener { docSnaps ->
                            val channels = arrayListOf<ChannelModel>()
                            docSnaps.documents.forEach { snapshot ->
                                snapshot.toObject(ChannelModel::class.java)?.let { channels.add(it) }
                            }
                            continuation.resume(Result.success(channels.size))
                        }
                        .addOnFailureListener {
                            ReportHandler.reportError(it)
                            continuation.resume(Result.success(0))
                        }
                } catch (e: Exception) {
                    ReportHandler.reportError(e)
                    continuation.resume(Result.failure(e.toUnknown()))
                }
            }
        }
    }

    override suspend fun delete(notificationModel: NotificationModel): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                notifications.document(notificationModel.id).delete().await()
                Result.success(Unit)
            } catch (e: Exception) {
                ReportHandler.reportError(e)
                Result.failure(e)
            }
        }
    }
}
package com.jarlingwar.adminapp.domain.repositories.remote

import com.jarlingwar.adminapp.domain.models.NotificationModel
import kotlinx.coroutines.flow.Flow

interface IChatRepository {
    fun getNotificationQueueAsFlow(): Flow<List<NotificationModel>>
    suspend fun getChatChannels(userId: String): Result<Int>
    suspend fun delete(notificationModel: NotificationModel): Result<Unit>
    suspend fun deleteUserData(userId: String): Result<Unit>
}
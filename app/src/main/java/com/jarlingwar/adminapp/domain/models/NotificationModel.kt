package com.jarlingwar.adminapp.domain.models

import java.util.UUID

data class NotificationModel(
    val id: String = UUID.randomUUID().toString(),
    val receiverFcmToken: String,
    val receiverId: String,
    val senderId: String,
    val senderName: String,
    val senderImgUrl: String,
    val messageBody: String,
    val timestamp: Long = System.currentTimeMillis()
)
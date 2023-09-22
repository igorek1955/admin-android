package com.jarlingwar.adminapp.domain.models


data class NotificationModel(
    var id: String = "",
    var receiverFcmToken: String = "",
    var receiverId: String = "",
    var senderId: String = "",
    var senderName: String = "",
    var senderImgUrl: String = "",
    var messageBody: String = "",
    var timestamp: Long = 0
)
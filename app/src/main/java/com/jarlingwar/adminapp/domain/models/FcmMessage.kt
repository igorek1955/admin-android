package com.jarlingwar.adminapp.domain.models

const val FCM_TYPE_KEY = "fcm_type"

//data fields
const val FCM_RECEIVER_ID = "receiverId"
const val FCM_SENDER_ID = "senderId"
const val FCM_SENDER_IMG_URL = "senderImgUrl"
const val FCM_TITLE = "title"
const val FCM_BODY = "body"

enum class FcmMessageType {
    MESSAGE,
    CHANNELS,
    IN_APP_MESSAGE,
    DEFAULT
}

data class FcmMessage(
    val message: FcmMessageBody
) {
    constructor(receiver: UserModel, title: String, message: String) : this(
        message = FcmMessageBody(
            receiver.fcmToken,
            data = mapOf(FCM_TYPE_KEY to FcmMessageType.DEFAULT.name),
            FcmNotificationBody(message, title)
        )
    )

    constructor(
        receiver: UserModel,
        sender: UserModel,
        title: String,
        messageBody: String) : this(
        message = FcmMessageBody(
            token = receiver.fcmToken,
            data = mapOf(
                FCM_RECEIVER_ID to receiver.userId,
                FCM_SENDER_ID to sender.userId,
                FCM_TITLE to title,
                FCM_BODY to messageBody,
                FCM_SENDER_IMG_URL to sender.profileImageUrl,
                FCM_TYPE_KEY to FcmMessageType.MESSAGE.name
            ),
            notification = null
        )
    )
}

data class FcmMessageBody(
    val token: String,
    val data: Map<String, String>,
    val notification: FcmNotificationBody?,
    val apns: FcmApns = FcmApns()
) {
    data class FcmApns(
        val payload: FcmPayload = FcmPayload()
    ) {
        data class FcmPayload(
            val aps: Map<String, String> = hashMapOf("empty" to "empty")
        )
    }
}

data class FcmNotificationBody(
    val body: String,
    val title: String
)

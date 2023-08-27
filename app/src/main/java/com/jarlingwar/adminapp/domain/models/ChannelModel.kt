package com.jarlingwar.adminapp.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * for one conversation there are two ChannelModels
 */
@Parcelize
data class ChannelModel(
    var id: String,
    var userA: String,
    var userB: String,
    var userBName: String = "",
    var userBImageUrl: String,
    var lastMessageBody: String,
    var messageIsFromB: Boolean = false,
    var unreadMessageNum: Int,
    var lastUpdateTime: Long,
): Parcelable {
    constructor() : this(
        id = "",
        userA = "",
        userB = "",
        userBImageUrl = "",
        lastMessageBody = "",
        unreadMessageNum = 0,
        lastUpdateTime = 0
    )
    constructor(userAID: String, userB: UserModel): this(
        id = UUID.randomUUID().toString(),
        userA = userAID,
        userB = userB.userId,
        userBImageUrl = userB.profileImageUrl,
        userBName = userB.displayName,
        lastMessageBody = "",
        unreadMessageNum = 0,
        lastUpdateTime = System.currentTimeMillis()
    )
}
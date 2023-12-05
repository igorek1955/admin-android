package com.jarlingwar.adminapp.domain.models

import java.util.Random
import java.util.UUID

data class ReviewModel(
    var id: String,
    var userId: String,
    var reviewerId: String,
    var reviewerImageUrl: String,
    var reviewerName: String,
    var created: Long,
    var approved: Boolean = false,
    var rating: Float,
    var body: String
) {
    companion object {
        fun getMock(): ReviewModel {
            val user = UserModel.getMock()
            val rating = Random().nextInt(5).toFloat()
            var mockText = ""
            repeat(10) {
                mockText += UUID.randomUUID().toString() + " "
            }
            return ReviewModel(UUID.randomUUID().toString(), user, rating, mockText)
        }
    }
    constructor() : this(
        id = "",
        userId = "",
        reviewerId = "",
        reviewerImageUrl = "",
        reviewerName = "",
        created = 0L,
        rating = 0f,
        body = ""
    )

    constructor(userId: String, reviewer: UserModel, rating: Float, message: String) : this(
        id = UUID.randomUUID().toString(),
        userId = userId,
        reviewerId = reviewer.userId,
        reviewerImageUrl = reviewer.profileImageUrl,
        reviewerName = reviewer.displayName,
        created = System.currentTimeMillis(),
        rating = rating,
        body = message
    )
}
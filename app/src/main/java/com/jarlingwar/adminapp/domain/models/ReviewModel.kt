package com.jarlingwar.adminapp.domain.models

import java.util.UUID

data class ReviewModel(
    var id: String,
    var userId: String,
    var reviewerId: String,
    var reviewerImageUrl: String,
    var reviewerName: String,
    var timestamp: Long,
    var isApproved: Boolean = false,
    var rating: Float,
    var body: String
) {
    constructor() : this(
        id = "",
        userId = "",
        reviewerId = "",
        reviewerImageUrl = "",
        reviewerName = "",
        timestamp = 0L,
        rating = 0f,
        body = ""
    )

    constructor(userId: String, reviewer: UserModel, rating: Float, message: String) : this(
        id = UUID.randomUUID().toString(),
        userId = userId,
        reviewerId = reviewer.userId,
        reviewerImageUrl = reviewer.profileImageUrl,
        reviewerName = reviewer.displayName,
        timestamp = System.currentTimeMillis(),
        rating = rating,
        body = message
    )
}
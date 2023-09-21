package com.jarlingwar.adminapp.utils

object SPTypes {
    const val APP_ID = "appId"
}

object FirestoreCollections {
    const val USERS = "users"
    const val LISTINGS = "listings"
    const val BLOCKED_USERS = "blockedUsers"
    const val CHANNELS = "channels"
    const val REVIEWS = "reviews"
    const val IMAGES = "images"
    const val REPORTS = "reports"
    const val NOTIFICATIONS = "notifications"
}

object ChatFields {
    const val USER_A = "userA"
    const val USER_B = "userB"
    const val SENDER_ID = "senderId"
    const val RECEIVER_ID = "receiverId"
    const val CHANNEL_ID = "channelId"
    const val MESSAGE_IS_SEEN = "seen"
    const val MESSAGE_DELETED_BY = "deletedByUser"
}

object ReviewFields {
    const val ID = "id"
    const val USER_ID = "userId"
    const val REVIEWER_ID = "reviewerId"
    const val CREATED = "created"
    const val APPROVED = "approved"
}

object ReportFields {
    const val LAST_REPORTED = "lastReported"
    const val PROCESSED = "processed"
}

object UserFields {
    const val EMAIL = "email"
    const val UID = "userId"
    const val NAME = "displayName"
    const val CREATED = "created"
    const val UPDATED = "updated"
    const val REPORTS = "reports"
}

object BlockedUserFields {
    const val EMAIL = "email"
    const val UID = "userId"
    const val CREATED = "created"
}

object ListingFields {
    const val ID = "listingId"
    const val TAGS = "tags"
    const val TITLE = "title"
    const val USER_ID = "userId"
    const val GEOHASH = "location.geoHash"
    const val GEOHASH5 = "location.geoHash5"
    const val GEOHASH4 = "location.geoHash4"
    const val GEOHASH3 = "location.geoHash3"
    const val COUNTRY = "location.country"
    const val COUNTRY_PART = "location.countryPart"
    const val CREATED = "created"
    const val REACTIONS = "reactions"
    const val CATEGORY = "category"
    const val PRICE = "price"
    const val REPORTS = "reports"
    const val STATUS = "status"
    const val APPROVED = "approved"
}

object BuildType {
    const val RELEASE = "release"
    const val DEBUG = "debug"
}
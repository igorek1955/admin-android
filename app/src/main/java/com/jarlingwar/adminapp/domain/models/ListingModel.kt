package com.jarlingwar.adminapp.domain.models

import android.os.Parcelable
import com.jarlingwar.adminapp.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListingModel(
    var listingId: String = "",
    val userId: String = "",
    var category: String = "",
    var approved: Boolean = false,
    var localImgUrlList: List<String> = emptyList(),
    var remoteImgUrlList: List<String> = emptyList(),
    var price: Long = 0,
    var title: String = "",
    var status: ListingStatus = ListingStatus.UNPUBLISHED,
    var rejectReason: String? = null,
    var description: String = "",
    var created: Long = 0,
    var updated: Long = created,
    var views: Int = 0,
    var reactions: Int = 0,
    var reports: Int = 0,
    var location: LocationModel? = null,
    var contactInfo: String = "",
    //used for recommendations and search, created from title
    var tags: List<String>? = emptyList()
): Parcelable {
    companion object {
        fun getMock(): ListingModel {
            return ListingModel(
                title = "Test title",
                category = "category",
                created = System.currentTimeMillis(),
                price = 123423,
                location = LocationModel(
                    latitude = 0.0,
                    longitude = 0.0,
                    locationName = "Moscow, Russia",
                    country = "Russia"
                )
            )
        }
    }
}

enum class ListingStatus(val resId: Int) {
    SOLD(R.string.sold),
    PUBLISHED(R.string.published),
    REJECTED(R.string.rejected),
    UNPUBLISHED(R.string.unpublished)
}

enum class RejectReason(val resId: Int) {
    INAPPROPRIATE_CONTENT(R.string.rejection_inappropriate),
    SPAM(R.string.rejection_spam),
    THIRD_PARTY_AD(R.string.rejection_third_party),
    INACCURATE_DESCRIPTION(R.string.rejection_misleading)
}


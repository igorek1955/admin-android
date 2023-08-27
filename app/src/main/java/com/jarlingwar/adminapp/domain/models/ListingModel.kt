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
                title = "Test title super long title 23 title",
                category = "service",
                created = System.currentTimeMillis(),
                price = 123423,
                reports = 23,
                views = 112,
                reactions = 4,
                contactInfo = "Fdfas dafadf123 231231 r324fdsaf fd132132fdds 3232",
                description = "d dsafasfsdf fdasfdas fsadfasfdasf afsddsafdsaf fsdafsdferdew vfafefvs fasdf ewfsfdsvasvdsfasd fewfsadfsdaf afewafdsfadsfawef dsfsafaefsdfas",
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

enum class RejectReason(val resId: Int, var text: String = "") {
    INAPPROPRIATE_CONTENT(R.string.rejection_inappropriate),
    SPAM(R.string.rejection_spam),
    THIRD_PARTY_AD(R.string.rejection_third_party),
    INACCURATE_DESCRIPTION(R.string.rejection_misleading),
    CUSTOM(R.string.rejection_other)
}


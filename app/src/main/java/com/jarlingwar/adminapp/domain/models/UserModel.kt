package com.jarlingwar.adminapp.domain.models

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val userId: String = "",
    var email: String = "",
    var displayName: String = "",
    var about: String = "",
    var blocked: Boolean = false,
    var blockedUsers: List<String> = listOf(),
    var verified: Boolean = false,
    var anonymous: Boolean = false,
    var online: Boolean = false,
    var hasFullAccess: Boolean = false,
    var fcmToken: String = "",
    var created: Long = 0,
    var updated: Long = 0,
    var reports: Int = 0,
    var lastSessionTime: Long = 0,
    var profileImageUrl: String = "",
    var geoHash: String = "",
    var interests: List<String> = emptyList(),
    var favorites: List<String> = emptyList(),
    var listingsId: List<String> = emptyList(),
    var reviews: List<Float> = emptyList()
): Parcelable {
    companion object {
        fun getMock(): UserModel {
            return UserModel(
                userId = "fsdafdsf12332",
                email = "yandex@test.ru",
                displayName = "Petr Petrovich",
                about = "my name is pupa and i shave my zalupa",
                verified = true,
                created = System.currentTimeMillis() - 1_000_000_000,
                updated = System.currentTimeMillis() - 1_000_000,
                lastSessionTime = System.currentTimeMillis(),
                profileImageUrl = "https://api.dicebear.com/6.x/micah/jpg?size=130&seed=1faff",
                reviews = listOf(1.2f, 4.2f, 5f, 4f),
                listingsId = listOf("2","3"),
                favorites = listOf("432", "421ff"),
                geoHash = "ucfv0n014x7c",
                reports = 2323
            )
        }
        fun getUserModelFromFirebase(firebaseUser: FirebaseUser): UserModel {
            return UserModel(
                userId = firebaseUser.uid,
                email = firebaseUser.email?: "",
                displayName = firebaseUser.displayName?: "",
                interests = arrayListOf(),
                about = "",
                verified = firebaseUser.isEmailVerified,
                created = firebaseUser.metadata?.creationTimestamp?: 0,
                lastSessionTime = firebaseUser.metadata?.lastSignInTimestamp?: 0,
                profileImageUrl = (firebaseUser.photoUrl?: "").toString()
            )
        }
    }
}


data class UserViewData(
    val userModel: UserModel = UserModel(),
    var locationName: String = "",
    var ratingsPublished: String = "",
    var usedLocations: List<String> = emptyList(),
    var channelsNum: String = ""
)

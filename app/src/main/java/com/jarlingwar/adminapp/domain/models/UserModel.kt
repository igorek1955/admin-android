package com.jarlingwar.adminapp.domain.models

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val userId: String = "",
    var email: String = "",
    var phone: String = "",
    var displayName: String = "",
    var about: String = "",
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
    var interests: ArrayList<String> = arrayListOf(),
    var favorites: ArrayList<String> = arrayListOf(),
    var listingsId: ArrayList<String> = arrayListOf(),
    var ratings: List<Float> = emptyList()
): Parcelable {
    companion object {
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

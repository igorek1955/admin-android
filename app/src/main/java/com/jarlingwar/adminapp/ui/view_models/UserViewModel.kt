package com.jarlingwar.adminapp.ui.view_models

import android.app.Application
import android.location.Address
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jarlingwar.adminapp.domain.ListingManager
import com.jarlingwar.adminapp.domain.UserManager
import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.domain.models.UserViewData
import com.jarlingwar.adminapp.domain.repositories.remote.IChatRepository
import com.jarlingwar.adminapp.domain.repositories.remote.IReviewRepository
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.geo.GeoDecoder
import com.jarlingwar.adminapp.utils.geo.geohash.GeoHash
import com.jarlingwar.adminapp.utils.toUnknown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    application: Application,
    private val userManager: UserManager,
    private val listingManager: ListingManager,
    private val reviewRepo: IReviewRepository,
    private val chatRepo: IChatRepository,
    private val geoDecoder: GeoDecoder
) : AndroidViewModel(application) {

    var listings by mutableStateOf<List<ListingModel>>(emptyList())
    var error by mutableStateOf<CustomError?>(null)
    var isLoading by mutableStateOf(false)
    var isNoResults by mutableStateOf(false)
    var isDeleteSuccess by mutableStateOf(false)
    var isUserBlocked by mutableStateOf(false)
    var userData by mutableStateOf(UserViewData())

    fun init(user: UserModel) {
        userData = userData.copy(userModel = user)
        loadListings()
        viewModelScope.launch(Dispatchers.IO) {

            userManager.getBlockStatus(user.userId)
                .onSuccess { isUserBlocked = it }

            chatRepo.getChatChannels(user.userId)
                .onSuccess { userData = userData.copy(channelsNum = it.toString()) }

            reviewRepo.getUserAuthoredReviews(user.userId)
                .onSuccess { userData = userData.copy(ratingsPublished = it.size.toString()) }

            listingManager.getListings(user.listingsId)
                .onSuccess { results ->
                    val countries = results.mapNotNull { listing -> listing.location?.country }.distinct()
                    userData = userData.copy(usedLocations = countries)
                }

            val loc = GeoHash(user.geoHash).toLocation()
            geoDecoder.decodeLocation(loc, object : GeoDecoder.ResultListener {
                override fun onLocationReady(address: Address) {
                    userData = userData.copy(locationName = address.adminArea)
                }
            })
        }
    }

    fun blockAndDelete() {
        block()
        deleteUserData()
    }

    fun block() {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            userManager.blockUser(userData.userModel.userId, userData.userModel.email)
                .onSuccess {
                    isLoading = false
                    isUserBlocked = true
                }
        }
    }

    fun unblock() {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            userManager.unblockUser(userData.userModel.userId)
                .onSuccess {
                    isLoading = false
                    isUserBlocked = false
                }
        }
    }

    //delete
    fun deleteUserData() {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            listingManager.deleteListings(userData.userModel.listingsId).getOrNull()
            userManager.deleteUser(userData.userModel)
                .onSuccess { isDeleteSuccess = true }
                .onFailure { error = it.toUnknown() }
        }
    }

    fun verify() {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            val u = userData.userModel.copy(verified = true)
            userManager.saveUser(u)
                .onSuccess {
                    isLoading = false
                    userData = userData.copy(userModel = u)
                }
                .onFailure {
                    isLoading = false
                    error = it.toUnknown()
                }
        }
    }

    private fun loadListings() {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            val listingIds = userData.userModel.listingsId
            if (listingIds.isNotEmpty()) {
                listingManager.getListings(listingIds)
                    .onSuccess {
                        isLoading = false
                        listings = it
                        isNoResults = listings.isEmpty()
                    }
                    .onFailure {
                        isLoading = false
                        error = it.toUnknown()
                    }
            }
        }
    }
}
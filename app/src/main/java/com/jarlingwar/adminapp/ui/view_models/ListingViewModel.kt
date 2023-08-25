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
import com.jarlingwar.adminapp.domain.models.ListingStatus
import com.jarlingwar.adminapp.domain.models.RejectReason
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.geo.GeoDecoder
import com.jarlingwar.adminapp.utils.geo.geohash.GeoHash
import com.jarlingwar.adminapp.utils.toUnknown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingViewModel @Inject constructor(
    application: Application,
    private val listingManager: ListingManager,
    private val userManager: UserManager,
    private val geoDecoder: GeoDecoder
): AndroidViewModel(application) {

    var listing by mutableStateOf(ListingModel())
    var listingUser by mutableStateOf<UserModel?>(null)
    var locationName by mutableStateOf("")
    var isSuccess by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var isDeleteSuccess by mutableStateOf(false)
    var error by mutableStateOf<CustomError?>(null)

    fun init(listing: ListingModel) {
        this.listing = listing
        viewModelScope.launch(Dispatchers.IO) {
            listingUser = userManager.getUserById(listing.userId).getOrNull()
            listingUser?.geoHash?.let { hash ->
                val loc = GeoHash(hash).toLocation()
                geoDecoder.decodeLocation(loc, object : GeoDecoder.ResultListener {
                    override fun onLocationReady(address: Address) {
                        locationName = address.adminArea
                    }
                })
            }
        }
    }

    fun approve() {
        isLoading = true
        val approvedListing = listing.copy(approved = true)
        viewModelScope.launch(Dispatchers.IO) {
            listingManager.saveListing(approvedListing)
                .onSuccess {
                    isSuccess = true
                    isLoading = false
                    listing = approvedListing
                }
                .onFailure { error = it.toUnknown() }
        }
    }

    fun reject(reason: RejectReason) {
        isLoading = true
        val rejectedListing = listing.copy(
            approved = false,
            rejectReason = reason.text,
            status = ListingStatus.REJECTED
        )
        viewModelScope.launch(Dispatchers.IO) {
            listingManager.saveListing(rejectedListing)
                .onSuccess {
                    isSuccess = true
                    isLoading = false
                    listing = rejectedListing
                }
                .onFailure { error = it.toUnknown() }
        }
    }

    fun deleteListing() {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            listingManager.deleteListing(listing)
                .onSuccess { isDeleteSuccess = true }
                .onFailure { error = it.toUnknown() }
        }
    }

    fun deleteAllUserData() {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            if (listingUser != null) {
                listingManager.deleteListings(listingUser!!.listingsId).getOrNull()
                userManager.deleteUser(listingUser!!)
                    .onSuccess { isDeleteSuccess = true }
                    .onFailure { error = it.toUnknown() }
            } else {
                listingManager.deleteListing(listing)
                    .onSuccess { isDeleteSuccess = true }
                    .onFailure { error = it.toUnknown() }
            }
        }
    }
}
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
    var success by mutableStateOf(false)
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
        success = false
        val approvedListing = listing.copy(approved = true)
        viewModelScope.launch(Dispatchers.IO) {
            listingManager.saveListing(approvedListing)
                .onSuccess { success = true }
                .onFailure { error = it.toUnknown() }
        }
    }

    fun reject(reason: RejectReason) {
        success = false
        val rejectedListing = listing.copy(
            approved = false,
            rejectReason = reason.text,
            status = ListingStatus.REJECTED
        )
        viewModelScope.launch(Dispatchers.IO) {
            listingManager.saveListing(rejectedListing)
                .onSuccess { success = true }
                .onFailure { error = it.toUnknown() }
        }
    }

    fun deleteListing() {
        success = false
        viewModelScope.launch(Dispatchers.IO) {
            listingManager.deleteListing(listing)
                .onSuccess { success = true }
                .onFailure { error = it.toUnknown() }
        }
    }

    fun deleteAndBlockUser() {
        success = false
        viewModelScope.launch(Dispatchers.IO) {
            listingUser?.let { userManager.deleteUser(it).getOrNull() }
            listingManager.deleteListing(listing)
                .onSuccess { success = true }
                .onFailure { error = it.toUnknown() }
        }
    }
}
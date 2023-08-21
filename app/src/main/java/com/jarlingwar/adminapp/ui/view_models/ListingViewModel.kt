package com.jarlingwar.adminapp.ui.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jarlingwar.adminapp.domain.ListingManager
import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.domain.models.RejectReason
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListingViewModel @Inject constructor(
    application: Application,
    private val listingManager: ListingManager
): AndroidViewModel(application) {
    lateinit var listing: ListingModel
    fun init(listing: ListingModel) {
        this.listing = listing
    }

    fun approve() {

    }

    fun reject(reason: RejectReason) {

    }

    fun delete() {

    }

    fun deleteAndBlockUser() {

    }
}
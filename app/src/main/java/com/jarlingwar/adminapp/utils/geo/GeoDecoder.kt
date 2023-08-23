package com.jarlingwar.adminapp.utils.geo

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GeoDecoder @Inject constructor(
    private val geoCoder: Geocoder
) {
    lateinit var listener: ResultListener
    interface ResultListener {
        fun onLocationReady(address: Address)
    }
    fun decodeLocation(location: Location, listener: ResultListener) {
        this.listener = listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geoCoder.getFromLocation(location.latitude, location.longitude, 1
            ) { p0 -> p0.firstOrNull()?.let { listener.onLocationReady(it) } }
        } else {
            geoCoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()?.let {
                listener.onLocationReady(it)
            }
        }
    }
}
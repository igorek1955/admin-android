package com.jarlingwar.adminapp.domain.models

import android.os.Parcelable
import com.jarlingwar.adminapp.utils.geo.CountryInfo
import com.jarlingwar.adminapp.utils.geo.capitalized
import com.jarlingwar.adminapp.utils.geo.geohash.GeoHash
import com.jarlingwar.adminapp.utils.geo.safeSubstring
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class LocationModel(
    var latitude: Double,
    var longitude: Double,
    var id: String = UUID.randomUUID().toString(),
    var radius: Double = 0.0,
    var locationName: String,
    var country: String = getCountry(locationName),
    var countryPart: String = getCountryPart(locationName),
    var geoHash: String = "",
    var geoHash3: String = "",
    var geoHash4: String = "",
    var geoHash5: String = "",
    var fullAddress: String = locationName
): Parcelable {
    constructor(): this(latitude = 0.0, longitude = 0.0, locationName = "")
    init { setGeoHash() }

    private fun setGeoHash() {
        geoHash = GeoHash(latitude, longitude).toString()
        geoHash3 = geoHash.safeSubstring(3)
        geoHash4 = geoHash.safeSubstring(4)
        geoHash5 = geoHash.safeSubstring(5)
    }

    companion object {
        private fun getCountry(locationName: String): String {
            return if (locationName.isEmpty()) {
                CountryInfo.values().first().name.capitalized()
            } else {
                val arr = locationName.split(", ")
                arr.lastOrNull()?: ""
            }
        }
        private fun getCountryPart(locationName: String): String {
            val arr = locationName.split(", ")
            return if (arr.size >= 2) {
                arr[arr.size - 2]
            } else if (arr.size == 1) arr[0]
            else ""
        }
    }
}
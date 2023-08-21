package com.jarlingwar.adminapp.utils.geo

import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.domain.models.LocationModel
import java.util.Locale

enum class CountryInfo(val currency: String, val titleResId: Int) {
    INTERNATIONAL("$", R.string.international),
    RUSSIA("₽", R.string.russia),
    INDIA("₹", R.string.india),
    THAILAND("฿", R.string.thailand),
    TURKEY("₺", R.string.turkey)
}

fun String.getCurrency(): String {
    return getCountryInfo().currency
}

fun String.getTitleResId(): Int {
    return getCountryInfo().titleResId
}

fun LocationModel?.getCurrency(): String {
    return this?.country?.getCurrency() ?: CountryInfo.INTERNATIONAL.currency
}

fun String.capitalized() = this.lowercase().replaceFirstChar {
    it.titlecase(Locale.getDefault())
}

fun LocationModel?.getTitleResId(): Int {
    return this?.country?.getTitleResId() ?: CountryInfo.INTERNATIONAL.titleResId
}

fun String.getCountryInfo(): CountryInfo {
    return when (this.uppercase()) {
        CountryInfo.INDIA.name -> CountryInfo.INDIA
        CountryInfo.THAILAND.name -> CountryInfo.THAILAND
        CountryInfo.RUSSIA.name -> CountryInfo.RUSSIA
        else -> CountryInfo.INTERNATIONAL
    }
}
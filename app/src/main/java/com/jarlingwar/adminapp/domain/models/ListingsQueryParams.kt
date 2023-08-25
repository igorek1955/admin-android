package com.jarlingwar.adminapp.domain.models

import com.google.firebase.firestore.Query
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.utils.ListingFields
import com.jarlingwar.adminapp.utils.geo.CountryInfo


data class ListingsQueryParams(
    var country: CountryInfo? = CountryInfo.INTERNATIONAL,
    var orderBy: SortOrder? = SortOrder.CREATED_DESC
) {
    fun update(newParams: ListingsQueryParams) {
        newParams.country?.let { country = it }
        newParams.orderBy?.let { orderBy = it }
    }
}

//to avoid errors empty field usage must be avoided
enum class SortOrder(
    val titleResId: Int,
    val fieldName: String,
    val direction: Query.Direction) {
    CREATED_DESC(R.string.filter_most_recent, ListingFields.CREATED, Query.Direction.DESCENDING),
    PRICE_DESC(R.string.filter_price_desc, ListingFields.PRICE, Query.Direction.DESCENDING),
    PRICE_ASC(R.string.filter_price_asc, ListingFields.PRICE, Query.Direction.ASCENDING),
    POPULARITY_DESC(R.string.filter_pupolarity, ListingFields.REACTIONS, Query.Direction.DESCENDING),
    REPORTS(R.string.reports, ListingFields.REPORTS, Query.Direction.DESCENDING)
}
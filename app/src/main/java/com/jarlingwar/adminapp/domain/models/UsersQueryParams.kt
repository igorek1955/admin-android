package com.jarlingwar.adminapp.domain.models

import com.google.firebase.firestore.Query
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.utils.ListingFields
import com.jarlingwar.adminapp.utils.UserFields
import com.jarlingwar.adminapp.utils.geo.CountryInfo


data class UsersQueryParams(
    var country: CountryInfo = CountryInfo.INTERNATIONAL,
    var orderBy: UsersSortOrder = UsersSortOrder.CREATED_DESC
) {
    fun update(newParams: UsersQueryParams) {
        country = newParams.country
        orderBy = newParams.orderBy
    }
}

//to avoid errors empty field usage must be avoided
enum class UsersSortOrder(
    val titleResId: Int,
    val fieldName: String,
    val direction: Query.Direction) {
    CREATED_DESC(R.string.filter_most_recent, UserFields.CREATED, Query.Direction.DESCENDING),
    UPDATED_DESC(R.string.by_update_time, UserFields.UPDATED, Query.Direction.DESCENDING),
    REPORTS(R.string.reports, UserFields.REPORTS, Query.Direction.DESCENDING)
}
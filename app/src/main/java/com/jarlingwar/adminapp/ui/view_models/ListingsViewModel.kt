package com.jarlingwar.adminapp.ui.view_models

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jarlingwar.adminapp.domain.ListingManager
import com.jarlingwar.adminapp.domain.UserManager
import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.domain.models.ListingsQueryParams
import com.jarlingwar.adminapp.domain.models.SortOrder
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.utils.AbstractPager
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.Pager
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.geo.CountryInfo
import com.jarlingwar.adminapp.utils.toUnknown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ListingsViewModel @Inject constructor(
    application: Application,
    private val listingManager: ListingManager,
    private val userManager: UserManager,
) : AndroidViewModel(application) {

    var listings by mutableStateOf<List<ListingModel>>(emptyList())
    val params get() = listingManager.getParams()
    var isPendingListings = true
    var error by mutableStateOf<CustomError?>(null)
    var isLoading by mutableStateOf(false)
    var isLoadingNext by mutableStateOf(false)
    var isRefreshing by mutableStateOf(false)
    var currentUser by mutableStateOf<UserModel?>(null)
    private var pager: Pager<ListingModel>? = null

    fun init(isPending: Boolean) {
        isLoading = true
        isPendingListings = isPending
        startPaging()
        viewModelScope.launch(Dispatchers.IO) {
            userManager.userInfoFlow.collectLatest { currentUser = it }
        }
    }

    fun updateCountry(pos: Int) {
        CountryInfo.values().getOrNull(pos)?.let { newCountry ->
            val updatedParams = ListingsQueryParams(country = newCountry)
            listingManager.updateParams(updatedParams)
            refresh()
        }
    }

    fun updateSortOrder(pos: Int) {
        SortOrder.values().getOrNull(pos)?.let { newOrder ->
            listingManager.updateParams(order = newOrder)
            refresh()
        }
    }

    fun loadNext() {
        pager?.loadNext()
    }

    fun refresh() {
        isRefreshing = true
        startPaging()
    }

    private fun startPaging() {
        if (pager == null) {
            setupPager()
        }
        pager?.reload()
    }

    private fun setupPager() {
        pager = Pager(
            scope = viewModelScope,
            pager = object : AbstractPager<ListingModel>() {
                override fun onError(t: Throwable) {
                    ReportHandler.reportError(t)
                    error = t.toUnknown()
                }
                override fun onLoadNext() { isLoadingNext = true }
                override fun onSuccess(result: List<ListingModel?>) {
                    isLoading = false
                    isLoadingNext = false
                    isRefreshing = false
                    listings = result.filterNotNull()
                }

                override fun onNoResults() {
                   isLoading = false
                }
            }, pagingFlow = {
                if (isPendingListings) listingManager.getPendingListingsPaging(it)
                else listingManager.getPublishedListingsPaging(it)
            })
    }
}
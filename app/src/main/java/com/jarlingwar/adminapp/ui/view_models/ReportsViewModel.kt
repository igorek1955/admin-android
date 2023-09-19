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
import com.jarlingwar.adminapp.domain.models.ReportModel
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.domain.repositories.remote.IReportRepository
import com.jarlingwar.adminapp.utils.AbstractPager
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.Pager
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.toUnknown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    application: Application,
    private val repo: IReportRepository,
    private val listingManager: ListingManager,
    private val userManager: UserManager
) : AndroidViewModel(application) {

    var reports by mutableStateOf<List<ReportModel>>(emptyList())
    var error by mutableStateOf<CustomError?>(null)
    var isLoading by mutableStateOf(false)
    var isLoadingNext by mutableStateOf(false)
    var isRefreshing by mutableStateOf(false)
    var currentUser by mutableStateOf<UserModel?>(null)
    var reportedListing by mutableStateOf<ListingModel?>(null)
    var reportedUser by mutableStateOf<UserModel?>(null)

    private var pager: Pager<ReportModel>? = null

    fun init() {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            userManager.userInfoFlow.collectLatest { currentUser = it }
        }
        startPaging()
    }

    fun getListing(listingId: String) {
        viewModelScope.launch {
            listingManager.getListings(listOf(listingId))
                .onSuccess { reportedListing = it.firstOrNull() }
                .onFailure { error = it.toUnknown() }
        }
    }

    fun getUser(userId: String) {
        viewModelScope.launch {
            userManager.getUserById(userId)
                .onSuccess { reportedUser = it }
                .onFailure { error = it.toUnknown() }
        }
    }

    fun delete(report: ReportModel) {
        viewModelScope.launch { repo.delete(report) }
    }

    fun update(report: ReportModel) {
        viewModelScope.launch { repo.update(report) }
    }

    fun loadNext() {
        pager?.loadNext()
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
            pager = object : AbstractPager<ReportModel>() {
                override fun onError(t: Throwable) {
                    ReportHandler.reportError(t)
                    error = t.toUnknown()
                }

                override fun onLoadNext() {
                    isLoadingNext = true
                }

                override fun onSuccess(result: List<ReportModel?>) {
                    isLoading = false
                    isLoadingNext = false
                    isRefreshing = false
                    reports = result.filterNotNull()
                }

                override fun onNoResults() {
                    isLoading = false
                }
            }, pagingFlow = {
                repo.getReportsPaging(it)
            })
    }
}
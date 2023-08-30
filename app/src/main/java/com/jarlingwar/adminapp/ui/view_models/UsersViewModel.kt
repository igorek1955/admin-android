package com.jarlingwar.adminapp.ui.view_models

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jarlingwar.adminapp.domain.UserManager
import com.jarlingwar.adminapp.domain.models.SortOrder
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.domain.models.UsersSortOrder
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
class UsersViewModel @Inject constructor(
    application: Application,
    private val userManager: UserManager
) : AndroidViewModel(application) {
    var users by mutableStateOf<List<UserModel>>(emptyList())
    val params get() = userManager.getParams()
    var error by mutableStateOf<CustomError?>(null)
    var isLoading by mutableStateOf(false)
    var isLoadingNext by mutableStateOf(false)
    var isRefreshing by mutableStateOf(false)
    var currentUser by mutableStateOf<UserModel?>(null)
    private var pager: Pager<UserModel>? = null


    fun init() {
        isLoading = true
        startPaging()
        viewModelScope.launch(Dispatchers.IO) {
            userManager.userInfoFlow.collectLatest { currentUser = it }
        }
    }

    fun updateSortOrder(pos: Int) {
        UsersSortOrder.values().getOrNull(pos)?.let { newOrder ->
            val newParams = params.copy(orderBy = newOrder)
            userManager.updateParams(newParams)
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

    private fun setupPager() {
        pager = Pager(
            scope = viewModelScope,
            pager = object : AbstractPager<UserModel>() {
                override fun onError(t: Throwable) {
                    ReportHandler.reportError(t)
                    error = t.toUnknown()
                }
                override fun onLoadNext() { isLoadingNext = true }
                override fun onSuccess(result: List<UserModel?>) {
                    isLoading = false
                    isLoadingNext = false
                    isRefreshing = false
                    users = result.filterNotNull()
                }
            }, pagingFlow = { userManager.getUsersPaging(it) })
    }


    private fun startPaging() {
        if (pager == null) {
            setupPager()
        }
        pager?.reload()
    }
}
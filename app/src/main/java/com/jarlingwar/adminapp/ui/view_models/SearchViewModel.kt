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
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.toUnknown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application,
    private val userManager: UserManager,
    private val listingManager: ListingManager
) : AndroidViewModel(application) {
    var listings by mutableStateOf(emptyList<ListingModel>())
    var users by mutableStateOf(emptyList<UserModel>())
    var error by mutableStateOf<CustomError?>(null)
    var isLoading by mutableStateOf(false)
    var isNoResults by mutableStateOf(false)
    var currentUser by mutableStateOf<UserModel?>(null)

    enum class SearchType {
        LISTINGS,
        USERS
    }

    enum class ListingField {
        TAGS,
        ID
    }

    enum class UserField {
        ID,
        EMAIL,
        NAME
    }

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            currentUser = userManager.userInfoFlow.value
        }
    }

    fun searchListings(query: String, field: ListingField) {
        isNoResults = false
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = if (field == ListingField.TAGS) {
                listingManager.searchListings(query, 100)
            } else listingManager.getListings(listOf(query))
            result
                .onSuccess {
                    isLoading = false
                    if (it.isEmpty()) isNoResults = true
                    listings = it
                }
                .onFailure {
                    isLoading = false
                    error = it.toUnknown()
                    ReportHandler.reportError(it)
                }
        }
    }

    fun searchUsers(query: String, field: UserField) {
        fun processResult(result: Result<List<UserModel>>) {
            result
                .onSuccess { users ->
                    isLoading = false
                    if (users.isEmpty()) isNoResults = true
                    this@SearchViewModel.users = users
                }
                .onFailure {
                    isLoading = false
                    error = it.toUnknown()
                }
        }
        isNoResults = false
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            when (field) {
                UserField.EMAIL -> {
                    val res = userManager.getUsersByEmail(query)
                    processResult(res)
                }
                UserField.NAME -> {
                    val res = userManager.getUsersByName(query)
                    processResult(res)
                }
                UserField.ID -> {
                    userManager.getUserById(query)
                        .onSuccess { user ->
                            isLoading = false
                            if (user != null) users = listOf(user)
                            else isNoResults = true
                        }
                        .onFailure {
                            isLoading = false
                            error = it.toUnknown()
                        }
                }
            }
        }
    }
}
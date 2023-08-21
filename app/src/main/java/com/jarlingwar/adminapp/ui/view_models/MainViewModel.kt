package com.jarlingwar.adminapp.ui.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jarlingwar.adminapp.domain.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val userManager: UserManager
): AndroidViewModel(application) {
    val isAuthRequired = MutableStateFlow<Boolean?>(null)
    init {
        viewModelScope.launch(Dispatchers.IO) {
            userManager.initData()
        }
        viewModelScope.launch(Dispatchers.IO) {
            userManager.userInfoFlow.collectLatest { user ->
                if (userManager.isInitialized) {
                    if (user?.userId.isNullOrEmpty() || user?.hasFullAccess == false) {
                        isAuthRequired.update { true }
                    } else isAuthRequired.update { false }
                }
            }
        }
    }
}
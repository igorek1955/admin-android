package com.jarlingwar.adminapp.ui.view_models

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.domain.UserManager
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.RemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val userManager: UserManager,
    private val remoteConfig: RemoteConfig
) : AndroidViewModel(application) {

    enum class Screen {
        AUTH,
        LOGIN_SUCCESS,
        RESET_SUCCESS,
        REGISTRATION_SUCCESS,
        MISSING_PERMISSIONS
    }

    enum class FieldError(val resId: Int) {
        INCORRECT_EMAIL(R.string.error_email),
        PASSWORD(R.string.error_password),
        PASSWORD_MATCH(R.string.error_pass_match),
        NAME(R.string.error_name)
    }

    var screenType by mutableStateOf(Screen.AUTH)
    var error by mutableStateOf<CustomError?>(null)
    var isLoading by mutableStateOf(false)
    var fieldError by mutableStateOf<FieldError?>(null)

    private var googleSignInClient: GoogleSignInClient? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userManager.userInfoFlow.collectLatest { user ->
                if (!user?.userId.isNullOrEmpty()) {
                    screenType = if (user!!.hasFullAccess) Screen.LOGIN_SUCCESS
                    else Screen.MISSING_PERMISSIONS
                }
            }
        }
    }

    fun retryLogin() {
        viewModelScope.launch {
            userManager.logout()
            screenType = Screen.AUTH
        }
    }

    /**
     * 1 - attempting to sign in
     * 2 - on success - saving UserInfo to remote and local database
     */
    fun verifyUserLoginInfo(email: String, password: String) {
        val areFieldsValid = validateFields(email, password)
        if (!areFieldsValid) return
        error = null
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            userManager.authenticate(email, password)
                .onSuccess {
                    userManager.initData(it?.userId)
                    isLoading = false
                }
                .onFailure {
                    error = CustomError.AuthError.Login(it)
                    isLoading = false
                }
        }
    }

    fun registerNewUser(email: String, password: String, password2: String, displayName: String) {
        val areFieldsValid = validateFields(email, password, password2, displayName)
        if (!areFieldsValid) return
        error = null
        isLoading = true
        viewModelScope.launch {
            userManager.registerUser(email, password, displayName) {
                screenType = Screen.REGISTRATION_SUCCESS
                isLoading = false
            }.onFailure {
                error = CustomError.AuthError.Register(it)
                isLoading = false
            }
        }
    }

    fun getSignInIntent() = googleSignInClient?.signInIntent

    fun setupGoogleAuth(context: Context) {
        if (googleSignInClient != null) return
        val clientId = remoteConfig.oauthClientId
        if (clientId.isEmpty()) {
            error = CustomError.newError("empty oauthClientId")
            return
        }
        val googleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(clientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
    }

    fun resetPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            error = null
            isLoading = true
            userManager.resetPassword(email)
                .onSuccess {
                    isLoading = false
                    screenType = Screen.RESET_SUCCESS
                }.onFailure {
                    isLoading = false
                    error = CustomError.AuthError.Reset(it.message ?: "")
                }
        }
    }

    /**
     * 1 - processing GoogleSignIn response
     * 2 - sing in with GOOGLE credentials to firebase
     * 3 - on success - save user to database (NOT NEW USER - no need to send email confirmation)
     */
    fun processGoogleSignInResult(data: Intent) {
        error = null
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            userManager.authenticateGoogle(data)
                .onSuccess {
                    it?.let { user -> userManager.initData(user = user) }
                    isLoading = false
                }.onFailure {
                    isLoading = false
                    error = CustomError.AuthError.Login(it)
                }
        }
    }

    private fun validateFields(
        email: String,
        password: String,
        password2: String? = null,
        name: String? = null
    ): Boolean {
        fieldError = null
        return when {
            email.isEmpty() ||
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                fieldError = FieldError.INCORRECT_EMAIL
                false
            }
            password.length < 8 -> {
                fieldError = FieldError.PASSWORD
                false
            }
            password2 != null && password2 != password -> {
                fieldError = FieldError.PASSWORD_MATCH
                false
            }
            name != null && name.length < 4 -> {
                fieldError = FieldError.NAME
                false
            }
            else -> true
        }
    }
}
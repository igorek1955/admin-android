package com.jarlingwar.adminapp.utils

import com.jarlingwar.adminapp.R
import kotlinx.coroutines.flow.MutableStateFlow

fun Throwable?.toUnknown(message: String = ""): CustomError {
    if (this is CustomError) return this
    return CustomError.GeneralError.Unknown(this ?: Exception(), message = message)
}

suspend fun MutableStateFlow<CustomError?>.emitCustom(error: Throwable?) {
    if (error is CustomError) emit(error)
    else emit(error.toUnknown())
}

sealed class CustomError(message: String = "", throwable: Throwable = Throwable(message)): Throwable(message, throwable) {
    companion object {
        val NETWORK_ERROR = GeneralError.NoConnection()
        val LIMIT_REACHED = GeneralError.ListingLimitReached()
        val USER_NOT_FOUND = GeneralError.UserNotFound()
        val AUTH_REQUIRED = AuthError.AuthRequired()
        fun newError(message: String): CustomError {
            return GeneralError.Unknown(message = message)
        }
        fun getResIdByErrorType(error: Throwable): Int {
            val errorMessage = error.message ?: ""
            return when {
                errorMessage.contains(ErrorTypes.MISSING_QUERY_INDEX) -> R.string.error_missing_index
                errorMessage.contains(ErrorTypes.EMAIL_TAKEN) -> R.string.error_email_taken
                errorMessage.contains(ErrorTypes.INVALID_USER) ||
                        errorMessage.contains(ErrorTypes.WRONG_PASS_OR_LOGIN) -> R.string.password_or_user_incorrect
                else -> R.string.error_unknown
            }
        }
    }
    open var resId: Int = 0
    sealed class ListingError(message: String = ""): CustomError(message) {
        class ListingNotFound(message: String = "", override var resId: Int = R.string.error_no_listing): ListingError(message)
        class NoResults(message: String = "", override var resId: Int = R.string.error_no_results): ListingError(message)
    }


    sealed class GeneralError(message: String = "", throwable: Throwable = Throwable(message)): CustomError(message, throwable) {
        class UserNotFound(message: String = "", override var resId: Int = R.string.error_user_not_found): CustomError(message)
        class ListingLimitReached(override var resId: Int = R.string.error_listings_limit): GeneralError()
        class NoResults(message: String = ""): GeneralError(message)
        class NoConnection(message: String = "", override var resId: Int = R.string.error_network): GeneralError(message)
        class LocationNotFound(message: String = "", override var resId: Int = R.string.error_location_not_found): GeneralError(message)
        class LocationServiceError(message: String = "", override var resId: Int = R.string.error_location_not_found): GeneralError(message)
        class Unknown(
            throwable: Throwable = Throwable(),
            message: String = throwable.message ?: "",
            override var resId: Int = getResIdByErrorType(throwable),
        ): GeneralError(message = throwable.message ?: message, throwable)
    }

    sealed class PermissionError(message: String = ""): CustomError(message) {
        class NoPermission(message: String = "", override var resId: Int = 0): GeneralError(message)
        class NoLocationPermission(message: String = "", override var resId: Int = R.string.error_no_location_permission): GeneralError(message)
        class NoCameraPermission(message: String = "", override var resId: Int = R.string.error_no_camera_permission): GeneralError(message)
        class NoNotificationPermission(message: String = "", override var resId: Int = R.string.error_no_notification_permission): GeneralError(message)
        class NoStoragePermission(message: String = "", override var resId: Int = R.string.error_no_storage_permission): GeneralError(message)
    }



    sealed class AuthError(message: String, throwable: Throwable = Throwable(message)): CustomError(message, throwable) {
        class Reset(message: String = ""): AuthError(message)
        class AuthRequired(message: String = "", override var resId: Int = R.string.auth_required): AuthError(message)
        class WrongLoginInfo(message: String = "", override var resId: Int = R.string.password_or_user_incorrect): AuthError(message)
        class Login(throwable: Throwable = Throwable(),
                    message: String = throwable.message ?: "",
                    override var resId: Int = getResIdByErrorType(throwable)): AuthError(message, throwable)
        class Register(
            throwable: Throwable = Throwable(),
            message: String = throwable.message ?: "",
            override var resId: Int = getResIdByErrorType(throwable)): AuthError(message, throwable)
    }
}

object ErrorTypes {
    const val INVALID_USER = "FirebaseAuthInvalidUserException"
    const val DEADLINE = "DEADLINE_EXCEEDED"
    const val EMAIL_TAKEN = "FirebaseAuthUserCollision"
    const val WRONG_PASS_OR_LOGIN = "FirebaseAuthInvalidCredentialsException"
    const val MISSING_QUERY_INDEX = "FAILED_PRECONDITION"
}
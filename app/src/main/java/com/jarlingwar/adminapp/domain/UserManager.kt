package com.jarlingwar.adminapp.domain

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.messaging.FirebaseMessaging
import com.jarlingwar.adminapp.domain.models.BlockedUser
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.domain.models.UsersQueryParams
import com.jarlingwar.adminapp.domain.repositories.remote.IUsersRepository
import com.jarlingwar.adminapp.domain.repositories.remote.UserResponse
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.toUnknown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface IUserManager {
    suspend fun registerUser(email: String, password: String, displayName: String, onSuccess: () -> Unit = {}): UserResponse
    suspend fun authenticate(email: String, password: String): UserResponse
    suspend fun resetPassword(email: String): Result<Boolean>
    suspend fun authenticateGoogle(data: Intent): UserResponse
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Boolean>
    suspend fun saveNewUser(userModel: UserModel): Result<Boolean>
    suspend fun saveUser(userModel: UserModel): Result<Boolean>
    suspend fun getUserById(userId: String): UserResponse
    suspend fun getUsersByEmail(email: String): Result<List<UserModel>>
    suspend fun getUsersByName(name: String): Result<List<UserModel>>
    suspend fun updateUserToken(token: String)
    suspend fun logout()
    suspend fun deleteUser(userModel: UserModel): Result<Boolean>
    suspend fun blockUser(id: String, email: String) : Result<Boolean>
    suspend fun getBlockStatus(id: String) : Result<Boolean>
    suspend fun unblockUser(id: String) : Result<Boolean>
    fun getUsersPaging(pagingReference: Flow<Int>) : Flow<List<UserModel>>
    fun updateParams(params: UsersQueryParams)
    fun getParams(): UsersQueryParams
}

@Singleton
class UserManager @Inject constructor(
    private val remoteStorage: IUsersRepository,
    private val firebaseAuth: FirebaseAuth
) : IUserManager {
    var userInfoFlow: MutableStateFlow<UserModel?> = MutableStateFlow(null)
    //checked only on start up
    var isInitialized = false
    companion object { private const val TAG = "UserManager" }
    override suspend fun registerUser(
        email: String,
        password: String,
        displayName: String,
        onSuccess: () -> Unit
    ): UserResponse {
        return remoteStorage.registerUser(email, password, displayName)
            .onSuccess { user ->
                onSuccess()
                firebaseAuth.currentUser?.sendEmailVerification()
                if (user != null) saveNewUser(user)
            }
    }

    override suspend fun authenticate(email: String, password: String): UserResponse {
        return remoteStorage.authenticateUser(email, password)
    }

    override suspend fun resetPassword(email: String) = remoteStorage.resetPassword(email)

    override suspend fun authenticateGoogle(data: Intent): UserResponse {
        return withContext(Dispatchers.IO) {
            try {
                suspendCoroutine { continuation ->
                    val signInAccountTask: Task<GoogleSignInAccount> = GoogleSignIn
                        .getSignedInAccountFromIntent(data)
                    if (signInAccountTask.isSuccessful) {
                        val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
                        val authCredential = GoogleAuthProvider.getCredential(
                            googleSignInAccount.idToken, null
                        )
                        firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener { task ->
                                val firebaseUser = task.result.user
                                if (task.isSuccessful && firebaseUser != null) {
                                    val user = UserModel.getUserModelFromFirebase(firebaseUser)
                                    continuation.resume(Result.success(user))
                                } else {
                                    continuation.resume(Result.failure(task.exception.toUnknown()))
                                }
                            }
                    }
                }
            } catch (e: Exception) {
                Result.failure(e.toUnknown())
            }
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val user = firebaseAuth.currentUser
                if (user == null || user.email.isNullOrEmpty()) {
                    Result.failure(CustomError.GeneralError.UserNotFound())
                } else {
                    suspendCoroutine { continuation ->
                        val email = user.email ?: ""
                        val credential = EmailAuthProvider.getCredential(email, oldPassword)
                        user.reauthenticate(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    user.updatePassword(newPassword)
                                        .addOnCompleteListener { updateTask ->
                                            if (updateTask.isSuccessful) {
                                                continuation.resume(Result.success(true))
                                            } else {
                                                continuation.resume(Result.failure(
                                                    CustomError.GeneralError.Unknown(
                                                        updateTask.exception ?: Throwable()
                                                    )
                                                ))
                                            }
                                        }
                                } else {
                                    continuation.resume(Result.failure(CustomError.AuthError.WrongLoginInfo()))
                                }
                            }
                    }
                }
            } catch (e: Exception) {
                ReportHandler.reportError(e, TAG)
                Result.failure(e)
            }
        }
    }

    /**
     * 1 - updating user profile (display name) on firebase
     * 2 - sending email verification from firebase
     * 3 - calling saveUser
     */
    override suspend fun saveNewUser(userModel: UserModel): Result<Boolean> {
        userModel.updated = System.currentTimeMillis()
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(userModel.displayName)
            .build()
        firebaseAuth.currentUser?.updateProfile(request)
            ?.addOnFailureListener { ReportHandler.reportError(it) }
        return saveUser(userModel)
    }

    /**
     * 1 - setting user.updated
     * 2 - saving user to firebase remote database
     */
    override suspend fun saveUser(userModel: UserModel): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val updatedUser = userModel.copy(updated = System.currentTimeMillis())
                userInfoFlow.update { updatedUser }
                remoteStorage.saveUser(updatedUser)
            } catch (e: Exception) {
                ReportHandler.reportError(e)
                Result.failure(e)
            }
        }
    }

    override suspend fun getUserById(userId: String): UserResponse {
        return try {
            remoteStorage.getUser(userId)
        } catch (e: Exception) {
            ReportHandler.reportError(e)
            Result.failure(e)
        }
    }

    override suspend fun getUsersByEmail(email: String): Result<List<UserModel>> {
        return try {
            remoteStorage.getUsersByEmail(email)
        } catch (e: Exception) {
            ReportHandler.reportError(e)
            Result.failure(e)
        }
    }

    override suspend fun getUsersByName(name: String): Result<List<UserModel>> {
        return try {
            remoteStorage.getUsersByName(name)
        } catch (e: Exception) {
            ReportHandler.reportError(e)
            Result.failure(e)
        }
    }

    override suspend fun updateUserToken(token: String) {
        userInfoFlow.value?.let { user ->
            user.fcmToken = token
            saveUser(user)
        }
    }

    override suspend fun logout() {
        userInfoFlow.value?.let {
            firebaseAuth.signOut()
            userInfoFlow.value = null
        }
    }

    override suspend fun deleteUser(userModel: UserModel): Result<Boolean> {
        return try {
            remoteStorage.deleteUser(userModel)
        } catch (e: Exception) {
            ReportHandler.reportError(e)
            Result.failure(e)
        }
    }

    override suspend fun blockUser(id: String, email: String) : Result<Boolean> {
        val blockedUser = BlockedUser(id, email, System.currentTimeMillis())
        return remoteStorage.blockUser(blockedUser)
    }
    override suspend fun getBlockStatus(id: String) = remoteStorage.getBlockStatus(id)
    override suspend fun unblockUser(id: String) = remoteStorage.unblockUser(id)
    override fun getUsersPaging(pagingReference: Flow<Int>) = remoteStorage.getUsersPaging(pagingReference)

    override fun updateParams(params: UsersQueryParams) { remoteStorage.updateParams(params) }
    override fun getParams() = remoteStorage.getParams()

    suspend fun initData(id: String? = null, user: UserModel? = null) {
        var userModel = user
        if (userModel == null) {
            val fbUser = firebaseAuth.currentUser
            val userId = id?: if (fbUser?.isAnonymous != true) fbUser?.uid else null
            if (!userId.isNullOrEmpty()) {
                userModel = remoteStorage.getUser(userId).getOrNull()
            }
        }
        isInitialized = true
        userModel?.let {
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            userModel.fcmToken = fcmToken
            userModel.lastSessionTime = System.currentTimeMillis()
            userModel.online = true
            if (!userModel.verified) {
                userModel.verified = firebaseAuth.currentUser?.isEmailVerified ?: false
            }
            saveUser(userModel)
        }
    }
}
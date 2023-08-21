package com.jarlingwar.adminapp.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.domain.repositories.remote.DeleteUserResponse
import com.jarlingwar.adminapp.domain.repositories.remote.IUsersRemoteRepository
import com.jarlingwar.adminapp.domain.repositories.remote.SaveUserResponse
import com.jarlingwar.adminapp.domain.repositories.remote.UserResponse
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.FirestoreCollections
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.UserFields
import com.jarlingwar.adminapp.utils.toUnknown
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class UsersRemoteRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    firestore: FirebaseFirestore
) : IUsersRemoteRepository {
    private val reference = firestore.collection(FirestoreCollections.USERS)

    override suspend fun saveUser(userModel: UserModel): SaveUserResponse {
        return try {
            reference
                .document(userModel.userId)
                .set(userModel)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            ReportHandler.reportError(e)
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(userModel: UserModel): DeleteUserResponse {
        return try {
            reference
                .document(userModel.userId)
                .delete()
                .await()
            Result.success(true)
        } catch (e: Exception) {
            ReportHandler.reportError(e)
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Boolean> {
        return try {
            var success = false
            var errorMessage = ""
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    if (it.isSuccessful) success = true
                    else errorMessage = it.exception?.message ?: ""
                }
                .await()
            if (success) Result.success(true)
            else Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(uid: String) = suspendCoroutine<UserResponse> { continuation ->
        reference
            .whereEqualTo(UserFields.UID, uid)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val documents = it.result.documents
                    if (documents.isNotEmpty()) {
                        val user = documents[0].toObject(UserModel::class.java)
                        continuation.resume(Result.success(user))
                    } else continuation.resume(Result.failure(CustomError.GeneralError.UserNotFound()))
                } else continuation.resume(Result.failure(it.exception.toUnknown()))
            }
            .addOnFailureListener { continuation.resume(Result.failure(it)) }
    }

    override suspend fun registerUser(
        email: String,
        password: String,
        displayName: String
    ): UserResponse {
        return suspendCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result.user?.let { firebaseUser ->
                            val userModel = UserModel.getUserModelFromFirebase(firebaseUser)
                            userModel.displayName = displayName
                            userModel.created = System.currentTimeMillis()
                            continuation.resume(Result.success(userModel))
                        } ?: continuation.resume(Result.failure(it.exception.toUnknown()))
                    } else {
                        continuation.resume(Result.failure(java.lang.Exception(it.exception)))
                    }
                }
        }
    }

    override suspend fun authenticateUser(email: String, password: String): UserResponse {
        return suspendCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result.user?.let { firebaseUser ->
                            val userModel = UserModel.getUserModelFromFirebase(firebaseUser)
                            userModel.lastSessionTime = System.currentTimeMillis()
                            continuation.resume(Result.success(userModel))
                        } ?: continuation.resume(Result.failure(it.exception.toUnknown()))
                    } else {
                        continuation.resume(Result.failure(Exception(it.exception)))
                    }
                }
        }
    }

    override suspend fun getAllUsers(): Result<List<UserModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getReportedUsers(): Result<List<UserModel>> {
        TODO("Not yet implemented")
    }
}
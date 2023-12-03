package com.jarlingwar.adminapp.data.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.jarlingwar.adminapp.domain.models.RemovedUser
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.domain.models.UsersQueryParams
import com.jarlingwar.adminapp.domain.repositories.remote.DeleteUserResponse
import com.jarlingwar.adminapp.domain.repositories.remote.IUsersRepository
import com.jarlingwar.adminapp.domain.repositories.remote.SaveUserResponse
import com.jarlingwar.adminapp.domain.repositories.remote.UserResponse
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.FirestoreCollections
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.UserFields
import com.jarlingwar.adminapp.utils.paginate
import com.jarlingwar.adminapp.utils.toUnknown
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class UsersRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    firestore: FirebaseFirestore
) : IUsersRepository {
    private val reference = firestore.collection(FirestoreCollections.USERS)
    private val blockedRef = firestore.collection(FirestoreCollections.BLOCKED_USERS)
    private val deletedRef = firestore.collection(FirestoreCollections.DELETED_USERS)
    private var params: UsersQueryParams = UsersQueryParams()
    override fun updateParams(queryParams: UsersQueryParams) { params.update(queryParams) }
    override fun getParams() = params

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
            val deleteData = RemovedUser(userModel.userId, userModel.email, System.currentTimeMillis())
            deletedRef
                .document(userModel.email)
                .set(deleteData)
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

    override suspend fun getUser(uid: String): UserResponse {
        val task = reference
            .whereEqualTo(UserFields.UID, uid)
            .get()
        return processUserTask(task)
    }

    override suspend fun getUsersByEmail(email: String): Result<List<UserModel>> {
        val task = reference
            .whereGreaterThanOrEqualTo(UserFields.EMAIL, email)
            .get()
        return processUsersTask(task)
    }

    override suspend fun getUsersByName(name: String): Result<List<UserModel>> {
        val task = reference
            .whereGreaterThanOrEqualTo(UserFields.NAME, name)
            .get()
        return processUsersTask(task)
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
                        it.exception?.let { ReportHandler.reportError(it) }
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
                        it.exception?.let { e -> ReportHandler.reportError(e) }
                        continuation.resume(Result.failure(Exception(it.exception)))
                    }
                }
        }
    }

    override suspend fun getAllUsers(): Result<List<UserModel>> {
        val task = reference.get()
        return processUsersTask(task)
    }

    override suspend fun getReportedUsers(): Result<List<UserModel>> {
        val task = reference
            .whereGreaterThan(UserFields.REPORTS, 0)
            .get()
        return processUsersTask(task)
    }

    override fun getUsersPaging(pagingReference: Flow<Int>): Flow<List<UserModel>> {
        return reference
            .orderBy(params.orderBy.fieldName, params.orderBy.direction)
            .paginate(pagingReference, 50)
            .map { docs -> docs.mapNotNull { it.toObject(UserModel::class.java) } }
    }

    override suspend fun blockUser(user: RemovedUser): Result<Boolean> {
        return try {
            blockedRef
                .document(user.userId)
                .set(user)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            ReportHandler.reportError(e)
            Result.failure(e)
        }
    }

    override suspend fun getBlockStatus(id: String): Result<Boolean> {
        return try {
            val user = blockedRef.document(id).get().await().toObject(RemovedUser::class.java)
            Result.success(user != null)
        } catch (e: Exception) {
            ReportHandler.reportError(e)
            Result.failure(e)
        }
    }

    override suspend fun unblockUser(id: String): Result<Boolean> {
        return try {
            blockedRef
                .document(id)
                .delete()
                .await()
            Result.success(true)
        } catch (e: Exception) {
            ReportHandler.reportError(e)
            Result.failure(e)
        }
    }


    private suspend fun processUserTask(task: Task<QuerySnapshot>): UserResponse {
        return suspendCoroutine { continuation ->
            task
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val documents = it.result.documents
                        if (documents.isNotEmpty()) {
                            val user = documents[0].toObject(UserModel::class.java)
                            continuation.resume(Result.success(user))
                        } else continuation.resume(Result.failure(CustomError.GeneralError.UserNotFound()))
                    } else continuation.resume(Result.failure(it.exception.toUnknown()))
                }
        }
    }

    private suspend fun processUsersTask(task: Task<QuerySnapshot>): Result<List<UserModel>> {
        return suspendCoroutine { continuation ->
            task
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val documents = it.result.documents
                        val users = arrayListOf<UserModel>()
                        documents.forEach { doc ->
                            val user = doc.toObject(UserModel::class.java)
                            user?.let { users.add(user) }
                        }
                        continuation.resume(Result.success(users))
                    } else continuation.resume(Result.failure(it.exception.toUnknown()))
                }
        }
    }
}
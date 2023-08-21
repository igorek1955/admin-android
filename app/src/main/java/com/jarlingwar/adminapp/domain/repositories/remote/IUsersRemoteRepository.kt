package com.jarlingwar.adminapp.domain.repositories.remote

import com.jarlingwar.adminapp.domain.models.UserModel

typealias SaveUserResponse = Result<Boolean>
typealias DeleteUserResponse = Result<Boolean>
typealias UserResponse = Result<UserModel?>

interface IUsersRemoteRepository {
    suspend fun saveUser(userModel: UserModel): SaveUserResponse
    suspend fun deleteUser(userModel: UserModel): DeleteUserResponse
    suspend fun resetPassword(email: String): Result<Boolean>
    suspend fun getUser(uid: String): UserResponse
    suspend fun registerUser(email: String, password: String, displayName: String): UserResponse
    suspend fun authenticateUser(email: String, password: String): UserResponse
    suspend fun getAllUsers() : Result<List<UserModel>>
    suspend fun getReportedUsers() : Result<List<UserModel>>
}
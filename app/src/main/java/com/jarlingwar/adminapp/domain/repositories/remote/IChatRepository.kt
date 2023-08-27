package com.jarlingwar.adminapp.domain.repositories.remote

interface IChatRepository {
    suspend fun getChatChannels(userId: String): Result<Int>
}
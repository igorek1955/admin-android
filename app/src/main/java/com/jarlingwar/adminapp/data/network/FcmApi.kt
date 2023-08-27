package com.jarlingwar.adminapp.data.network

import com.google.gson.JsonObject
import com.jarlingwar.adminapp.domain.models.FcmMessage
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApi {
    @POST("v1/projects/orgoazm/messages:send")
    suspend fun sendMessage(@Body message: FcmMessage): Response<JsonObject>
}
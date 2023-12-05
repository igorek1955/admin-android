package com.jarlingwar.adminapp.di

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.google.common.net.HttpHeaders
import com.google.gson.Gson
import com.jarlingwar.adminapp.data.network.FcmApi
import com.jarlingwar.adminapp.utils.ReportHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Arrays
import java.util.Properties
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        const val FCM_HTTP_CLIENT = "FcmHttpClient"
        const val FCM_API_NAME = "FcmApi"
        const val FCM_TOKEN_SCOPE = "https://www.googleapis.com/auth/firebase.messaging"
        const val FCM_API_URL = "https://fcm.googleapis.com"
    }

    @Singleton
    @Provides
    fun provideFcmApi(@Named(FCM_API_NAME) retrofit: Retrofit): FcmApi {
        return retrofit.create(FcmApi::class.java)
    }

    @Singleton
    @Provides
    @Named(FCM_API_NAME)
    fun provideFcmRetrofit(@Named(FCM_HTTP_CLIENT) okHttpClient: OkHttpClient): Retrofit {
        return createRetrofit(FCM_API_URL, Gson(), okHttpClient)
    }

    @Singleton
    @Provides
    @Named(FCM_HTTP_CLIENT)
    fun provideFcmHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(getFcmHeadersInterceptor(context))
            .addInterceptor(getLoggingInterceptor())
            .build()
    }


    private fun getLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level =  HttpLoggingInterceptor.Level.BODY
        }
    }

    private fun getFcmHeadersInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            requestBuilder
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header("Authorization", "Bearer ${getFcmAccessToken(context)}")
            chain.proceed(requestBuilder.build())
        }
    }

    private fun createRetrofit(baseUrl: String, gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
    }

    //token-service.json is created from google api website or firestore
    //and place it in app/src/main/assets
    private fun getFcmAccessToken(context: Context): String {
        try {
            val file = context.assets.open("token-service.json")
            val googleCredentials: GoogleCredentials = GoogleCredentials
                .fromStream(file)
                .createScoped(listOf(FCM_TOKEN_SCOPE))
            googleCredentials.refresh()
            googleCredentials.refreshAccessToken()
            val token = googleCredentials.accessToken.tokenValue
            return token
        } catch (e: Exception) {
            ReportHandler.reportError(e, "getFcmAccessToken")
        }
        return ""
    }
}
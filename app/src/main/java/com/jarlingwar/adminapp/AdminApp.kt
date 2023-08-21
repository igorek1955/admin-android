package com.jarlingwar.adminapp

import android.app.Application
import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.SPTypes
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import java.util.UUID

@HiltAndroidApp
class AdminApp: Application() {
    companion object {
        lateinit var appId: String
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        initApplication()
        ReportHandler.initialize()
    }

    private fun initApplication() {
        runBlocking {
            val sp = getSharedPreferences(SPTypes.APP_ID, Context.MODE_PRIVATE)
            val savedAppId = sp.getString(SPTypes.APP_ID, null)
            if (savedAppId == null) {
                appId = UUID.randomUUID().toString()
                sp.edit().putString(SPTypes.APP_ID, appId).apply()
            } else appId = savedAppId
        }
    }
}
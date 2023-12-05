package com.jarlingwar.adminapp.utils

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.jarlingwar.adminapp.AdminApp
import java.io.Serializable

class ReportHandler {

    object EventTypes {
        const val REMOTE_CONFIG_FETCH_FAILED = "remoteConfigFetchFailed"
    }

    private object ParamTypes {

    }

    object UserPropertyTypes {
        const val APP_ID = "app_id"
    }

    companion object {
        private var gAnalytics: FirebaseAnalytics? = null
        private const val TAG = "ReportHandler"

        @JvmStatic
        fun initialize() {
            setupFirebase()
            setUserProperty(UserPropertyTypes.APP_ID, AdminApp.appId)
        }

        @JvmStatic
        fun reportError(t: Throwable, vararg params: Serializable?) {
            val paramsStr = params.map { it.toString() }
            val message = "args:$paramsStr ex:${t.message}"
            Log.e(TAG, message, t)
            val e = Throwable(message, t)
            Firebase.crashlytics.recordException(e)
        }

        @JvmStatic
        fun reportError(caller: String, t: Throwable, args: String = "") {
            Log.e(TAG,"$caller args:$args errorMessage:${t.message}", t)
            Firebase.crashlytics.recordException(t)
        }

        @JvmStatic
        fun reportEvent(event: String, vararg params: Serializable?) {
            val paramsStr = params.map { it.toString() }
            Log.i(TAG,"$event args:${paramsStr}")
            val bundle = createParams(event, params).toBundle()
            gAnalytics?.logEvent(event, bundle)
        }

        fun logEvent(content: String) {
            Log.i(TAG, content)
        }

        fun logEvent(e: Throwable) {
            val msg = "${e.message} + ${e.printStackTrace()}"
            Log.i(TAG, msg)
        }

        @JvmStatic
        fun setUserProperty(propertyKey: String, value: String) {
            gAnalytics?.setUserProperty(propertyKey, value)
        }

        private fun createParams(event: String, params: Array<out Serializable?>): Map<String, Serializable?> {
            return HashMap<String, Serializable?>()
        }

        private fun setupFirebase() {
            FirebaseCrashlytics.getInstance().setUserId(AdminApp.appId)
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            gAnalytics = Firebase.analytics
        }

        private fun Map<String, Serializable?>.toBundle(): Bundle {
            return Bundle().apply {
                forEach {
                    putSerializable(it.key, it.value)
                }
            }
        }
    }
}

inline fun <reified R : Any> R?.getCaller(): String {
    return this?.run {
        return this::class.simpleName ?: this::class.hashCode().toString()
    } ?: ""
}
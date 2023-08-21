package com.jarlingwar.adminapp.utils

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.jarlingwar.adminapp.R

class RemoteConfig {

    private val firebaseRemoteConfig: FirebaseRemoteConfig

    companion object {
        private var remoteConfig: RemoteConfig? = null
        private val CACHE_EXPIRATION: Long = if (BuildConfig.DEBUG) 60L else 60L * 100L
        const val OAUTH_CLIENT_ID = "oauth_client_id"
        const val WEBSITE_LINK = "website_link"
        const val SHARE_URL_DEEPLINK = "share_url_deeplink"
        const val SHARE_URL_PREFIX = "share_url_prefix"
        const val SUPPORT_EMAIL = "support_limit"
        const val PAGINATION_LIMIT = "pagination_limit"
        const val LISTINGS_LIMIT = "listings_limit"
        const val RECENT_QUALIFIER = "recent_qualifier"
        const val QUERY_ATTEMPTS = "query_attempts"


        fun getRemoteConfig(): RemoteConfig {
            if (remoteConfig == null) remoteConfig = RemoteConfig().apply { updateConfig() }
            return remoteConfig as RemoteConfig
        }
    }

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = CACHE_EXPIRATION
        }
        firebaseRemoteConfig = Firebase.remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
    }

    val oauthClientId: String get() = firebaseRemoteConfig.getString(OAUTH_CLIENT_ID)
    val websiteLink: String get() = firebaseRemoteConfig.getString(WEBSITE_LINK)
    val shareUrlPrefix: String get() = firebaseRemoteConfig.getString(SHARE_URL_PREFIX)
    val shareUrlDeeplink: String get() = firebaseRemoteConfig.getString(SHARE_URL_DEEPLINK)
    val supportEmail: String get() = firebaseRemoteConfig.getString(SUPPORT_EMAIL)
    val paginationLimit: Long get() = firebaseRemoteConfig.getLong(PAGINATION_LIMIT)
    val listingsLimit: Int get() = firebaseRemoteConfig.getLong(LISTINGS_LIMIT).toInt()
    val queryAttempts: Int get() = firebaseRemoteConfig.getLong(QUERY_ATTEMPTS).toInt()
    val recentQualifier: Long get() = firebaseRemoteConfig.getLong(RECENT_QUALIFIER)

    fun updateConfig() {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (!task.isSuccessful ||
                    firebaseRemoteConfig.info.lastFetchStatus == FirebaseRemoteConfig.LAST_FETCH_STATUS_FAILURE) {
                    ReportHandler.reportEvent(ReportHandler.EventTypes.REMOTE_CONFIG_FETCH_FAILED)
                }
            }
    }
}
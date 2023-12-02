package com.jarlingwar.adminapp

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jarlingwar.adminapp.navigation.Destinations
import com.jarlingwar.adminapp.navigation.NavSetup
import com.jarlingwar.adminapp.services.IntentType
import com.jarlingwar.adminapp.services.MonitoringService
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.view_models.MainViewModel
import com.jarlingwar.adminapp.utils.ListingFields
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.clear
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminAppTheme {
                navController = rememberNavController()
                NavSetup(navController, mainViewModel.isAuthRequired, mainViewModel.listingFromIntent)
            }
        }
        setupService()
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.listingFromIntent.value = null
    }

    override fun onNewIntent(intent: Intent?) {
        if (!::navController.isInitialized) return
        super.onNewIntent(intent)
        if (intent != null) {
            when (intent.getStringExtra(MonitoringService.INTENT_TYPE)) {
                IntentType.NEW_REVIEW.name -> {
                    navController.navigate(Destinations.Reviews.route)
                }
                IntentType.NEW_LISTING.name -> {
                    val listingId = intent.getStringExtra(ListingFields.ID)
                    if (listingId != null) {
                        mainViewModel.getListing(listingId)
                    }
                }
            }
        }
        intent?.clear()
    }

    private fun setupService() {
        if (!isMonitoringServiceRunning()) {
            val intent = Intent(this, MonitoringService::class.java)
            startForegroundService(intent)
        }
    }

    private fun isMonitoringServiceRunning(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (process in activityManager.runningAppProcesses) {
            if (process.processName == applicationContext.packageName) {
                for (serviceName in process.pkgList) {
                    if (serviceName == MonitoringService::class.java.name) return true
                }
            }
        }
        return false
    }
}
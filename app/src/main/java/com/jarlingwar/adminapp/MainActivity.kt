package com.jarlingwar.adminapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jarlingwar.adminapp.navigation.Destinations
import com.jarlingwar.adminapp.navigation.NavSetup
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.view_models.MainViewModel
import com.jarlingwar.adminapp.utils.observeAndAction
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
                NavSetup(navController, mainViewModel.isAuthRequired)
            }
        }
    }
}
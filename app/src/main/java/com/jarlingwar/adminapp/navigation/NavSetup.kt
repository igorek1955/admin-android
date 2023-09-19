package com.jarlingwar.adminapp.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jarlingwar.adminapp.ui.common.SplashScreen
import com.jarlingwar.adminapp.ui.screens.auth.AuthScreen
import com.jarlingwar.adminapp.ui.screens.listing.ListingScreen
import com.jarlingwar.adminapp.ui.screens.listing_list.ListingsScreen
import com.jarlingwar.adminapp.ui.screens.reports.ReportsScreen
import com.jarlingwar.adminapp.ui.screens.reviews.ReviewsScreen
import com.jarlingwar.adminapp.ui.screens.reviews.UserReviewsScreen
import com.jarlingwar.adminapp.ui.screens.search.SearchScreen
import com.jarlingwar.adminapp.ui.screens.user.UserScreen
import com.jarlingwar.adminapp.ui.screens.user_list.UsersScreen
import com.jarlingwar.adminapp.ui.view_models.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun NavSetup(navController: NavHostController, startScreenControl: MutableStateFlow<Boolean?>) {
    val destination = when (startScreenControl.collectAsState().value) {
        null -> Destinations.Splash.route
        true -> Destinations.Auth.route
        false -> Destinations.PendingListings.route
    }
    NavHost(
        navController = navController,
        startDestination = destination,
        enterTransition = { scaleIn() },
        exitTransition = { scaleOut() },
    ) {
        composable(Destinations.Auth.route) {
            AuthScreen { navController.navigate(Destinations.PendingListings.route) }
        }
        composable(Destinations.Splash.route) {
            SplashScreen()
        }
        composable(Destinations.Users.route) { it ->
            val sharedViewModel: SharedViewModel = it.sharedViewModel(navController)
            UsersScreen(
                sharedViewModel = sharedViewModel,
                onNavigateToUser = { navController.navigate(Destinations.User.route) },
                onNavigate = { navController.navigate(it) }
            )
        }
        composable(Destinations.PublishedListings.route) {
            val sharedViewModel: SharedViewModel = it.sharedViewModel(navController)
            ListingsScreen(
                sharedViewModel = sharedViewModel,
                isPendingListings = false,
                onNavigateToListing = { navController.navigate(Destinations.Listing.route) },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(Destinations.PendingListings.route) {
            val sharedViewModel: SharedViewModel = it.sharedViewModel(navController)
            ListingsScreen(
                sharedViewModel = sharedViewModel,
                isPendingListings = true,
                onNavigateToListing = { navController.navigate(Destinations.Listing.route) },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        animatedRoute(Destinations.Listing.route) {
            val sharedViewModel: SharedViewModel = it.sharedViewModel(navController)
            ListingScreen(
                sharedViewModel = sharedViewModel,
                onUserTap = { navController.navigate(Destinations.User.route) },
                onBackTap = { navController.popBackStack() })
        }
        composable(Destinations.User.route) {
            val sharedViewModel: SharedViewModel = it.sharedViewModel(navController)
            UserScreen(
                sharedViewModel = sharedViewModel,
                onNavToReviews = { navController.navigate(Destinations.UserReviews.route) },
                onNavToListing = { navController.navigate(Destinations.Listing.route) }
            ) { navController.popBackStack() }
        }

        composable(Destinations.Reviews.route) {
            ReviewsScreen { navController.navigate(it) }
        }
        composable(Destinations.UserReviews.route) {
            val sharedViewModel: SharedViewModel = it.sharedViewModel(navController)
            UserReviewsScreen(sharedViewModel = sharedViewModel) { navController.popBackStack() }
        }
        composable(Destinations.Search.route) {
            val sharedViewModel: SharedViewModel = it.sharedViewModel(navController)
            SearchScreen(
                sharedViewModel = sharedViewModel,
                onNavigateToListing = { navController.navigate(Destinations.Listing.route) },
                onNavigateToUser = { navController.navigate(Destinations.User.route) },
                onNavigate =  { navController.navigate(it) }
            )
        }
        composable(Destinations.Reports.route) {
            val sharedViewModel: SharedViewModel = it.sharedViewModel(navController)
            ReportsScreen(
                sharedViewModel = sharedViewModel,
                onNavigateToListing = { navController.navigate(Destinations.Listing.route) },
                onNavigateToUser = { navController.navigate(Destinations.User.route) },
                onNavigate =  { navController.navigate(it) }
            )
        }
    }
}

private fun NavGraphBuilder.animatedRoute(
    route: String,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        enterTransition = { scaleIn() },
        exitTransition = { scaleOut() },
        content = content
    )
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = Destinations.PendingListings.route
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
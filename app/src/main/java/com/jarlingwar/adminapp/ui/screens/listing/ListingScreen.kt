package com.jarlingwar.adminapp.ui.screens.listing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.jarlingwar.adminapp.ui.view_models.ListingViewModel
import com.jarlingwar.adminapp.ui.view_models.SharedViewModel

@Composable
fun ListingScreen(
    sharedViewModel: SharedViewModel,
    viewModel: ListingViewModel,
    onUserTap: () -> Unit,
    onBackTap: () -> Unit
) {
    LaunchedEffect(Unit) {
        sharedViewModel.selectedListing?.let { viewModel.init(it) }
    }

}
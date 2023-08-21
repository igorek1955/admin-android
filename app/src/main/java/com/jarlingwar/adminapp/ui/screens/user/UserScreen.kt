package com.jarlingwar.adminapp.ui.screens.user

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.view_models.SharedViewModel

@Composable
fun UserScreen(sharedViewModel: SharedViewModel, onBackClick: () -> Unit) {
    LaunchedEffect(Unit) {
        Log.i("IGOR_LOG", "UserScreen user:${sharedViewModel.selectedUser?.userId} listing:${sharedViewModel.selectedListing?.listingId}")
    }
    Scaffold(topBar = {
        TopAppBar {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    modifier = Modifier.padding(start = 8.dp).size(24.dp),
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "iconBack",
                    tint = MaterialTheme.adminColors.fillPrimary
                )
            }
        }
    }) { _ ->

    }
}
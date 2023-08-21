package com.jarlingwar.adminapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.navigation.Destinations
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors

enum class DrawerItem(val titleId: Int, val route: String) {
    PENDING_LISTINGS(R.string.pending_listings, Destinations.PendingListings.route),
    PUBLISHED_LISTINGS(R.string.published_listings, Destinations.PublishedListings.route),
    USERS(R.string.users, Destinations.Users.route),
    REPORTED_USERS(R.string.reported_users, Destinations.ReportedUsers.route),
    REVIEWS(R.string.reviews, Destinations.Reviews.route),
    REPORTS(R.string.reports, Destinations.Reports.route),
    SEARCH_USER(R.string.search_users, Destinations.UserSearch.route),
    SEARCH_LISTING(R.string.search_listings, Destinations.ListingSearch.route)
}

@Composable
fun Drawer(
    email: String,
    imgUrl: String,
    selectedItem: DrawerItem,
    onNavigate: (String) -> Unit) {
    Column(
        Modifier
            .fillMaxHeight()
            .background(MaterialTheme.adminColors.backgroundSecondary)
    ) {
        DrawerHeader(email = email, imgUrl = imgUrl)
        DrawerBody(selectedItem = selectedItem, onNavigate = onNavigate)
    }
}

@Composable
private fun DrawerHeader(email: String, imgUrl: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(50.dp)) {
        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyImage(imgUrl = imgUrl, 100.dp, shape = CircleShape)
            Spacer(Modifier.height(10.dp))
            Text(text = email, style = Type.Header2)
        }
    }
}

@Composable
private fun DrawerBody(selectedItem: DrawerItem, onNavigate: (String) -> Unit) {
    val drawerItems = DrawerItem.values().toList()
    LazyColumn {
        items(drawerItems) { item ->
            val isSelected = selectedItem == item
            Row(Modifier
                .fillMaxWidth()
                .clickable { onNavigate(item.route) }
                .background(color = if (isSelected) MaterialTheme.adminColors.secondary else Color.Transparent)
                .padding(10.dp)
            ) {
                Text(
                    text = stringResource(id = item.titleId),
                    style = Type.Subtitle2,
                    color = MaterialTheme.adminColors.textPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DrawerPreview() {
    AdminAppTheme {
        Drawer("test@test.com", "", DrawerItem.PENDING_LISTINGS, onNavigate = { })
    }
}
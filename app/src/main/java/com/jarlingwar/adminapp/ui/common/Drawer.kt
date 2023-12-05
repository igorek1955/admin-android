package com.jarlingwar.adminapp.ui.common

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.BuildConfig
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.navigation.Destinations
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import kotlinx.coroutines.launch

enum class DrawerItem(val titleId: Int, val iconRes: Int, val route: String) {
    PENDING_LISTINGS(
        R.string.pending_listings,
        R.drawable.ic_pending,
        Destinations.PendingListings.route
    ),
    PUBLISHED_LISTINGS(
        R.string.published_listings,
        R.drawable.ic_published_listings,
        Destinations.PublishedListings.route
    ),
    SEARCH(R.string.search, R.drawable.ic_search, Destinations.Search.route),
    USERS(R.string.users, R.drawable.ic_users, Destinations.Users.route),
    REVIEWS(R.string.reviews, R.drawable.ic_pen, Destinations.Reviews.route),
    REPORTS(R.string.reports, R.drawable.ic_report, Destinations.Reports.route)
}

@Composable
fun Drawer(
    email: String,
    imgUrl: String,
    selectedItem: DrawerItem,
    onNavigate: (String) -> Unit
) {
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
            .padding(bottom = 50.dp)
    ) {

        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.adminColors.backgroundAltSecondary)
                    .padding(top = 5.dp, bottom = 10.dp)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIcon(size = 30.dp)
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = MaterialTheme.adminColors.textAltSecondary,
                    style = Type.Subtitle2
                )
            }

            MyImage(
                imgUrl = imgUrl, modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(80.dp), shape = CircleShape
            )
            Text(text = email, style = Type.Subtitle1)
        }
    }
}

@Composable
private fun DrawerBody(selectedItem: DrawerItem, onNavigate: (String) -> Unit) {
    val drawerItems = DrawerItem.values().toList()
    Box(modifier = Modifier.fillMaxHeight()) {
        LazyColumn {
            items(drawerItems) { item ->
                val isSelected = selectedItem == item
                Row(Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) MaterialTheme.adminColors.primary else MaterialTheme.adminColors.backgroundPrimary)
                    .clickable { onNavigate(item.route) }
                    .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = item.iconRes),
                        colorFilter = ColorFilter.tint(if (isSelected) Color.White else MaterialTheme.adminColors.fillAltPrimary),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(id = item.titleId),
                        style = Type.Subtitle2,
                        color = if (isSelected) MaterialTheme.adminColors.textAltPrimary else MaterialTheme.adminColors.textPrimary
                    )
                }
            }
        }
        Text(
            text = "ver:${BuildConfig.VERSION_NAME}",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(5.dp),
            color = MaterialTheme.adminColors.textSecondary
        )
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DrawerScaffold(
    currentUser: UserModel?,
    currentDestination: DrawerItem,
    onNavigate: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            DrawerTopBar(stringResource(id = currentDestination.titleId)) {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            Drawer(
                email = currentUser?.email ?: "",
                imgUrl = currentUser?.profileImageUrl ?: "",
                selectedItem = currentDestination,
                onNavigate = {
                    if (currentDestination.route != it) {
                        onNavigate(it)
                    } else {
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                    }
                }
            )
        },
        content = content
    )
}

@Composable
private fun AppIcon(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null
        )
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun DrawerPreview() {
    AdminAppTheme {
        Drawer("test@test.com", "", DrawerItem.PENDING_LISTINGS, onNavigate = { })
    }
}
package com.jarlingwar.adminapp.ui.screens.user

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.domain.models.UserViewData
import com.jarlingwar.adminapp.ui.common.LoadingDialog
import com.jarlingwar.adminapp.ui.common.LogDialog
import com.jarlingwar.adminapp.ui.common.MyIcon
import com.jarlingwar.adminapp.ui.common.MyImage
import com.jarlingwar.adminapp.ui.common.MySnack
import com.jarlingwar.adminapp.ui.common.TopBar
import com.jarlingwar.adminapp.ui.screens.listing_list.HorizontalListingItem
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.paddingPrimaryStartEnd
import com.jarlingwar.adminapp.ui.view_models.SharedViewModel
import com.jarlingwar.adminapp.ui.view_models.UserViewModel
import com.jarlingwar.adminapp.utils.getDateHyphen
import com.jarlingwar.adminapp.utils.getDateTime
import com.jarlingwar.adminapp.utils.prettyPrint
import com.jarlingwar.adminapp.utils.round

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UserScreen(
    viewModel: UserViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    onNavToReviews: () -> Unit,
    onNavToListing: () -> Unit,
    onNavBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        sharedViewModel.selectedUser?.let { user ->
            viewModel.init(user)
        }
    }
    val user = sharedViewModel.selectedUser!!
    Scaffold(
        topBar = { OptionsTopBar(viewModel = viewModel) { onNavBack() } }
    ) {
        LazyColumn(
            modifier = Modifier.paddingPrimaryStartEnd(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { UserCard(user, viewModel.isUserBlocked, onNavToReviews) }
            item { UserDetails(viewModel.userData, viewModel.listings) }
            items(items = viewModel.listings, key = { it.listingId }) { item ->
                HorizontalListingItem(listing = item) {
                    sharedViewModel.selectedListing = it
                    onNavToListing()
                }
            }
        }
        viewModel.error?.resId?.let { resId ->
            val text = if (resId > 0) stringResource(resId) else viewModel.error?.message ?: ""
            MySnack(text) { viewModel.error = null }
        }

        if (viewModel.isDeleteSuccess && viewModel.deleteLogs.isEmpty()) {
            LogDialog(log = "Data Delete Success") {
                viewModel.isDeleteSuccess = false
            }
        }
        if (viewModel.deleteLogs.isNotEmpty()) {
            LogDialog(log = viewModel.deleteLogs) {
                viewModel.deleteLogs = ""
            }
        }
        if (viewModel.isLoading) LoadingDialog()
    }
}

@Composable
private fun UserDetails(data: UserViewData, listings: List<ListingModel>) {
    val user = data.userModel
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        DetailsRow(fieldName = stringResource(R.string.id), fieldValue = user.userId)
        DetailsRow(
            fieldName = stringResource(R.string.registration_date),
            fieldValue = getDateHyphen(user.created)
        )
        DetailsRow(
            fieldName = stringResource(R.string.last_time_online),
            fieldValue = getDateTime(user.created)
        )
        DetailsRow(
            fieldName = stringResource(R.string.total_listings),
            fieldValue = listings.size.toString()
        )
        DetailsRow(
            fieldName = stringResource(R.string.favorite_listings),
            fieldValue = user.favorites.size.toString()
        )
        DetailsRow(
            fieldName = stringResource(id = R.string.reports),
            fieldValue = user.reports.toString()
        )
        DetailsRow(fieldName = stringResource(R.string.channels), fieldValue = data.channelsNum)
        DetailsRow(
            fieldName = stringResource(R.string.ratings_received),
            fieldValue = user.reviews.size.toString()
        )
        DetailsRow(
            fieldName = stringResource(R.string.ratings_published),
            fieldValue = data.ratingsPublished
        )
        DetailsRow(
            fieldName = stringResource(R.string.last_known_location),
            fieldValue = data.locationName
        )
        DetailsRow(
            fieldName = stringResource(R.string.user_countries),
            fieldValue = data.usedLocations.toString()
        )
        var showRawData by remember { mutableStateOf(false) }
        val rotation by animateFloatAsState(targetValue = if (showRawData) 180f else 0f, label = "")
        Column {
            Button(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.adminColors.backgroundPrimary),
                onClick = { showRawData = !showRawData }) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .rotate(rotation),
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    colorFilter = ColorFilter.tint(MaterialTheme.adminColors.fillAltPrimary),
                    contentDescription = null
                )
                Text(
                    text = stringResource(
                        if (!showRawData) R.string.show_raw_data
                        else R.string.hide_raw_data
                    ),
                    style = Type.Subtitle2M,
                    color = MaterialTheme.adminColors.textPrimary
                )
            }
            AnimatedVisibility(
                visible = showRawData,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(user.prettyPrint(), color = MaterialTheme.adminColors.textPrimary)
            }
        }
        Text(
            text = stringResource(id = R.string.published_listings),
            style = Type.Subtitle2M,
            color = MaterialTheme.adminColors.textPrimary
        )
    }
}

@Composable
private fun DetailsRow(fieldName: String, fieldValue: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = fieldName,
            style = Type.Body1,
            color = MaterialTheme.adminColors.textSecondary
        )
        Text(text = fieldValue, style = Type.Body1)
    }
}

@Composable
private fun UserCard(user: UserModel, isBlocked: Boolean, onNavToReviews: () -> Unit) {
    Column(Modifier.padding(top = 10.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Box(contentAlignment = Alignment.Center) {
                val profileBgColor = if (user.verified) MaterialTheme.adminColors.primary
                else MaterialTheme.adminColors.dangerPrimary
                Box(
                    modifier = Modifier
                        .background(profileBgColor, CircleShape)
                        .size(73.dp)
                )
                MyImage(
                    modifier = Modifier.size(70.dp),
                    imgUrl = user.profileImageUrl,
                    shape = CircleShape
                )
                if (isBlocked) {
                    Text(
                        text = "Blocked",
                        style = Type.Body2,
                        modifier = Modifier
                            .background(
                                MaterialTheme.adminColors.tertiary,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(3.dp)
                    )
                }
            }

            Column(
                Modifier
                    .padding(start = 5.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = user.email,
                    style = Type.Body1,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = user.displayName,
                    style = Type.Body1,
                    color = MaterialTheme.adminColors.textSecondary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                if (user.reviews.isNotEmpty()) {
                    Button(onClick = { onNavToReviews() }) {
                        Row {
                            MyIcon(R.drawable.ic_filled_star)
                            val ratingsText =
                                if (user.reviews.isNotEmpty()) "${user.reviews.size} - ${
                                    user.reviews.average().round()
                                }" else "0"
                            Text(
                                modifier = Modifier.padding(horizontal = 5.dp),
                                text = ratingsText,
                                style = Type.Body2
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun OptionsTopBar(
    viewModel: UserViewModel? = null,
    onBack: () -> Unit
) {
    var showOptionsMenu by remember { mutableStateOf(false) }
    TopBar(
        title = stringResource(id = R.string.user_managment),
        onBack = onBack
    ) {
        IconButton(onClick = { showOptionsMenu = true }) {
            Image(
                painter = painterResource(id = R.drawable.ic_options),
                colorFilter = ColorFilter.tint(MaterialTheme.adminColors.primary),
                contentDescription = "options"
            )
        }
        DropdownMenu(
            expanded = showOptionsMenu && viewModel?.isLoading == false,
            onDismissRequest = { showOptionsMenu = false }
        ) {
            val isUserBlocked = viewModel?.isUserBlocked ?: false
            DropdownMenuItem(onClick = { viewModel?.block() }, enabled = !isUserBlocked) {
                Text(
                    text = stringResource(R.string.block),
                    style = Type.Subtitle2
                )
            }
            DropdownMenuItem(onClick = { viewModel?.unblock() }, enabled = isUserBlocked) {
                Text(
                    text = stringResource(R.string.unblock),
                    style = Type.Subtitle2
                )
            }
            DropdownMenuItem(onClick = { viewModel?.blockAndDelete() }) {
                Text(
                    text = stringResource(R.string.block_and_delete),
                    style = Type.Subtitle2
                )
            }
            DropdownMenuItem(onClick = { viewModel?.deleteUserData() }) {
                Text(
                    text = stringResource(R.string.delete_all_data),
                    style = Type.Subtitle2
                )
            }
            DropdownMenuItem(onClick = { viewModel?.verify() }) {
                Text(
                    text = stringResource(R.string.verify),
                    style = Type.Subtitle2
                )
            }
        }
    }
}

@Preview
@Composable
private fun OptionsMenuPreview() {
    AdminAppTheme() {
        OptionsTopBar() {}
    }
}

@Preview(showBackground = true)
@Composable
private fun UserPreview() {
    AdminAppTheme {
        Column(Modifier.paddingPrimaryStartEnd()) {
            val user = UserModel.getMock()
            val userData = UserViewData(
                user,
                "Moscow",
                "23",
                listOf("Russia", "Thailand", "Turkey"),
                "3"
            )
            UserCard(user, true) { }
            val listings = listOf(ListingModel.getMock(), ListingModel.getMock())
            ListingModel.getMock()
            UserDetails(userData, listings)
        }
    }
}
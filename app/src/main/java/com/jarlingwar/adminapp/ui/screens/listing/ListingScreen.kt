package com.jarlingwar.adminapp.ui.screens.listing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.domain.models.ListingStatus
import com.jarlingwar.adminapp.domain.models.RejectReason
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.ui.common.ImageDialog
import com.jarlingwar.adminapp.ui.common.IndicatorLine
import com.jarlingwar.adminapp.ui.common.LoadingDialog
import com.jarlingwar.adminapp.ui.common.MyIcon
import com.jarlingwar.adminapp.ui.common.MyImage
import com.jarlingwar.adminapp.ui.common.MyInputField
import com.jarlingwar.adminapp.ui.common.MySnack
import com.jarlingwar.adminapp.ui.common.NeutralButton
import com.jarlingwar.adminapp.ui.common.PrimaryButton
import com.jarlingwar.adminapp.ui.common.SecondaryButton
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.paddingPrimaryStartEnd
import com.jarlingwar.adminapp.ui.view_models.ListingViewModel
import com.jarlingwar.adminapp.ui.view_models.SharedViewModel
import com.jarlingwar.adminapp.utils.geo.capitalized
import com.jarlingwar.adminapp.utils.geo.getCurrency
import com.jarlingwar.adminapp.utils.getDateHyphen
import com.jarlingwar.adminapp.utils.prettyPrint
import com.jarlingwar.adminapp.utils.round
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

val imageSize = 350.dp

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ListingScreen(
    sharedViewModel: SharedViewModel,
    viewModel: ListingViewModel = hiltViewModel(),
    onUserTap: () -> Unit,
    onBackTap: () -> Unit
) {
    LaunchedEffect(Unit) {
        sharedViewModel.selectedListing?.let { viewModel.init(it) }
    }
    val scrollState = rememberCollapsingToolbarScaffoldState()
    val progress = scrollState.toolbarState.progress
    val pagerState = rememberPagerState()
    var showImageDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }

    val listing = viewModel.listing
    CollapsingToolbarScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.adminColors.backgroundPrimary),
        state = scrollState,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .parallax(0.5f)
                    .height(imageSize)
            ) {
                val images = listing.remoteImgUrlList
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    count = images.size
                ) { page ->
                    val imgUrl = images.getOrNull(page) ?: ""
                    MyImage(
                        imgUrl = imgUrl,
                        modifier = Modifier
                            .height(imageSize)
                            .fillMaxWidth()
                            .clickable { showImageDialog = true },
                        contentScale = ContentScale.Crop,
                        shape = RoundedCornerShape(0.dp)
                    )
                }
                if (images.size > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 40.dp)
                    ) {
                        IndicatorLine(
                            pageCount = images.size,
                            pagerState = pagerState
                        )
                    }
                }
            }

            Row(
                Modifier
                    .background(MaterialTheme.adminColors.backgroundPrimary.copy(alpha = 1 - progress))
                    .paddingPrimaryStartEnd()
                    .fillMaxWidth()
                    .pin(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBackTap) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        colorFilter = ColorFilter.tint(MaterialTheme.adminColors.primary),
                        contentDescription = "close"
                    )
                }

                Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_options),
                            colorFilter = ColorFilter.tint(MaterialTheme.adminColors.primary),
                            contentDescription = "options"
                        )
                    }
                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false }
                    ) {
                        DropdownMenuItem(onClick = { viewModel.deleteListing() }) {
                            Text(
                                text = stringResource(R.string.delete_listing),
                                style = Type.Subtitle2
                            )
                        }
                        DropdownMenuItem(onClick = { viewModel.deleteAllUserData() }) {
                            Text(text = stringResource(R.string.block_user), style = Type.Subtitle2)
                        }
                        DropdownMenuItem(onClick = {
                            showRejectDialog = true
                        }) {
                            Text(
                                text = stringResource(R.string.reject),
                                style = Type.Subtitle2
                            )
                        }
                    }
                }
            }
            Text(
                text = viewModel.listing.title,
                color = MaterialTheme.adminColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = (18 + (30 - 18) * progress).sp,
                modifier = Modifier
                    .padding(3.dp)
                    .background(
                        MaterialTheme.adminColors.backgroundTertiary.copy(alpha = progress),
                        RoundedCornerShape(15.dp)
                    )
                    .padding(vertical = 5.dp)
                    .paddingPrimaryStartEnd()
                    .road(
                        whenCollapsed = Alignment.TopCenter,
                        whenExpanded = Alignment.BottomStart
                    )
            )
        },
        body = {
            ListingBody(
                listing = listing,
                user = viewModel.listingUser,
                userLocation = viewModel.locationName,
                onRejectTap = { showRejectDialog = true },
                onUserTap = {
                    sharedViewModel.selectedUser = viewModel.listingUser
                    onUserTap()
                },
                onApprove = { viewModel.approve() })
        })

    if (showImageDialog) {
        val imgUrl = listing.remoteImgUrlList.getOrNull(pagerState.currentPage)
        ImageDialog(imageUrl = imgUrl) {
            showImageDialog = false
        }
    }

    viewModel.error?.resId?.let { resId ->
        val text = if (resId > 0) stringResource(resId) else viewModel.error?.message ?: ""
        MySnack(text) { viewModel.error = null }
    }

    if (showRejectDialog) RejectDialog(onApply = {
        showRejectDialog = false
        viewModel.reject(it)
    }) {
        showRejectDialog = false
    }
    if (viewModel.isSuccess) MySnack(stringResource(R.string.action_success)) {
        viewModel.isSuccess = false
    }
    if (viewModel.isDeleteSuccess) onBackTap()
    if (viewModel.isLoading) LoadingDialog()
}

@Composable
private fun RejectDialog(onApply: (RejectReason) -> Unit, onDismissAction: () -> Unit) {
    val reasons = RejectReason.values()
    var selectedItem by remember { mutableStateOf(reasons.first()) }
    val otherReason = remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = { onDismissAction() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            Modifier
                .background(MaterialTheme.adminColors.backgroundPrimary, RoundedCornerShape(10.dp))
                .wrapContentHeight()
                .fillMaxWidth(0.8f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.reason),
                    style = Type.Subtitle2M,
                    color = MaterialTheme.adminColors.textPrimary
                )
            }
            reasons.forEach { reason ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedItem == reason,
                        onClick = { selectedItem = reason },
                        enabled = true,
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.adminColors.secondary)
                    )
                    Text(
                        text = stringResource(id = reason.resId),
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.adminColors.textPrimary
                    )
                }
            }
            if (selectedItem == RejectReason.CUSTOM) {
                MyInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    textVal = otherReason,
                    label = null,
                    placeholder = stringResource(id = R.string.description),
                    singleLine = false,
                    isClearEnabled = false
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                PrimaryButton(
                    text = stringResource(R.string.apply),
                    modifier = Modifier.weight(0.5f),
                    onTap = {
                        if (selectedItem == RejectReason.CUSTOM) {
                            selectedItem.text = otherReason.value
                        }
                        onApply(selectedItem)
                        onDismissAction()
                    }
                )
                Spacer(modifier = Modifier.width(2.dp))
                NeutralButton(
                    text = stringResource(R.string.cancel),
                    modifier = Modifier.weight(0.5f)
                ) { onDismissAction() }
            }
        }
    }
}


@Composable
private fun ListingBody(
    listing: ListingModel,
    user: UserModel?,
    userLocation: String,
    onRejectTap: () -> Unit,
    onUserTap: () -> Unit,
    onApprove: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .paddingPrimaryStartEnd()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "${listing.price}${listing.location.getCurrency()}",
            style = Type.Header2,
            color = MaterialTheme.adminColors.textPrimary
        )
        Divider(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .height(1.dp)
        )

        if (listing.status == ListingStatus.PUBLISHED && !listing.approved) {
            Row(Modifier.fillMaxWidth()) {
                PrimaryButton(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(40.dp),
                    text = stringResource(id = R.string.approve)
                ) { onApprove() }
                Spacer(modifier = Modifier.width(5.dp))
                SecondaryButton(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(40.dp),
                    text = stringResource(id = R.string.reject)
                ) { onRejectTap() }
            }
        }

        Row(Modifier.padding(vertical = 10.dp)) {
            Text(
                text = listing.status.name.capitalized(),
                style = Type.Subtitle2M,
                color = MaterialTheme.adminColors.textPrimary
            )
            if (listing.status == ListingStatus.REJECTED) {
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = "${listing.rejectReason}",
                    color = MaterialTheme.adminColors.textPrimary
                )
            }
        }

        Divider()
        user?.let {
            UserCard(user, listing, userLocation, onUserTap)
        }
        Divider()
        Spacer(modifier = Modifier.height(10.dp))

        //contact info
        Row(verticalAlignment = Alignment.CenterVertically) {
            MyIcon(iconRes = R.drawable.ic_contact)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.contact_info),
                style = Type.Body1M,
                color = MaterialTheme.adminColors.textSecondary
            )
        }
        Text(
            text = listing.contactInfo,
            style = Type.Body1,
            color = MaterialTheme.adminColors.textPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))
        Divider()

        //description
        Row(verticalAlignment = Alignment.CenterVertically) {
            MyIcon(iconRes = R.drawable.ic_details)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.description),
                style = Type.Body1M,
                color = MaterialTheme.adminColors.textSecondary
            )
        }
        Text(
            text = listing.description,
            style = Type.Body1,
            color = MaterialTheme.adminColors.textPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))
        Divider()

        //location
        Row(verticalAlignment = Alignment.CenterVertically) {
            MyIcon(iconRes = R.drawable.ic_location)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.location),
                style = Type.Body1M,
                color = MaterialTheme.adminColors.textSecondary
            )
        }
        Text(
            text = "${listing.location?.locationName} ${listing.location?.geoHash}",
            style = Type.Body1,
            color = MaterialTheme.adminColors.textPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))
        Divider()

        //details
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.category),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textSecondary
            )
            Text(
                text = listing.category.toString(),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textPrimary
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(id = R.string.published),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textSecondary
            )
            Text(
                text = getDateHyphen(listing.created),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textPrimary
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(id = R.string.last_updated),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textSecondary
            )
            Text(
                text = getDateHyphen(listing.updated),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textPrimary
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(id = R.string.views),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textSecondary
            )
            Text(
                text = listing.views.toString(),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textPrimary
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(id = R.string.added_to_favorites),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textSecondary
            )
            Text(
                text = listing.reactions.toString(),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textPrimary
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(id = R.string.reports),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textSecondary
            )
            Text(
                text = listing.reports.toString(),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textPrimary
            )
        }
        var showRawData by remember { mutableStateOf(false) }
        val rotation by animateFloatAsState(targetValue = if (showRawData) 180f else 0f, label = "")
        Column {
            Button(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(MaterialTheme.adminColors.backgroundSecondary)
                    .fillMaxWidth(),
                onClick = { showRawData = !showRawData }) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .rotate(rotation),
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = null
                )
                Text(
                    text = stringResource(
                        if (!showRawData) R.string.show_raw_data
                        else R.string.hide_raw_data
                    ),
                    style = Type.Subtitle2M,
                    color = MaterialTheme.adminColors.textAltPrimary
                )
            }
            AnimatedVisibility(
                visible = showRawData,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(listing.prettyPrint(), color = MaterialTheme.adminColors.textPrimary)
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun UserCard(
    user: UserModel,
    listing: ListingModel,
    userLocation: String,
    onUserTap: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onUserTap() }) {
        MyImage(
            modifier = Modifier.size(50.dp),
            imgUrl = user.profileImageUrl,
            shape = CircleShape
        )
        Column(Modifier.padding(horizontal = 10.dp)) {
            Text(
                text = user.displayName,
                style = Type.Body1,
                color = MaterialTheme.adminColors.textPrimary
            )
            Text(
                text = user.email,
                style = Type.Body1,
                color = MaterialTheme.adminColors.textPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                MyIcon(R.drawable.ic_filled_star)
                val ratingsText = if (user.reviews.isNotEmpty()) "${user.reviews.size}-${
                    user.reviews.average().round()
                }" else "0"
                Text(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    text = ratingsText,
                    style = Type.Body2,
                    color = MaterialTheme.adminColors.textPrimary
                )
                MyIcon(R.drawable.ic_new_user)
                Text(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    text = getDateHyphen(user.created),
                    style = Type.Body2,
                    color = MaterialTheme.adminColors.textPrimary
                )
                MyIcon(R.drawable.ic_report)
                Text(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    text = listing.reports.toString(),
                    style = Type.Body2,
                    color = MaterialTheme.adminColors.textPrimary
                )
            }
        }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MyIcon(iconRes = R.drawable.ic_location, MaterialTheme.adminColors.primary)
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = userLocation,
                    style = Type.Body1,
                    color = MaterialTheme.adminColors.textPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogPreview() {
    AdminAppTheme {
        RejectDialog(onApply = { }) { }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListingScreenPreview() {
    AdminAppTheme {
        val listing = ListingModel.getMock().copy(status = ListingStatus.PUBLISHED)
        ListingBody(listing, user = UserModel.getMock(), userLocation = "Moscow", {}, {}, {})
    }
}
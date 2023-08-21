package com.jarlingwar.adminapp.ui.screens.listing_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.domain.models.SortOrder
import com.jarlingwar.adminapp.ui.common.Drawer
import com.jarlingwar.adminapp.ui.common.DrawerItem
import com.jarlingwar.adminapp.ui.common.DrawerTopBar
import com.jarlingwar.adminapp.ui.common.DropDownTextMenu
import com.jarlingwar.adminapp.ui.common.LoadingIndicator
import com.jarlingwar.adminapp.ui.common.LoadingNextIndicator
import com.jarlingwar.adminapp.ui.common.NoResults
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.paddingPrimaryStartEnd
import com.jarlingwar.adminapp.ui.view_models.ListingsViewModel
import com.jarlingwar.adminapp.ui.view_models.SharedViewModel
import com.jarlingwar.adminapp.utils.geo.CountryInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListingsScreen(
    viewModel: ListingsViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel?,
    isPendingListings: Boolean = true,
    onListingTap: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(viewModel.isRefreshing, { viewModel.refresh() })
    LaunchedEffect(Unit) {
        viewModel.init(isPendingListings)
    }
    val currentItem = if (isPendingListings) DrawerItem.PENDING_LISTINGS
    else DrawerItem.PUBLISHED_LISTINGS
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            DrawerTopBar(stringResource(id = currentItem.titleId)) {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            Drawer(
                email = viewModel.currentUser?.email ?: "",
                imgUrl = viewModel.currentUser?.profileImageUrl ?: "",
                selectedItem = currentItem,
                onNavigate = onNavigate
            )
        },
        content = { paddingValues ->
            Column(
                Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .paddingPrimaryStartEnd()
            ) {
                val sortOrder = SortOrder.values().map { stringResource(id = it.titleResId) }
                val countries = CountryInfo.values().map { stringResource(id = it.titleResId) }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)) {
                    DropDownTextMenu(
                        Modifier.weight(0.5f), values = sortOrder, label = stringResource(
                            id = R.string.sorting
                        ),
                        selectedVal = stringResource(id = viewModel.params.orderBy!!.titleResId)
                    ) { viewModel.updateSortOrder(it) }
                    Spacer(modifier = Modifier.width(2.dp))
                    DropDownTextMenu(
                        Modifier.weight(0.5f), values = countries, label = stringResource(
                            id = R.string.country
                        ),
                        selectedVal = stringResource(id = viewModel.params.country!!.titleResId)
                    ) { viewModel.updateCountry(it) }
                }
                if (viewModel.isNoResults) {
                    NoResults()
                } else {
                    Box(Modifier.pullRefresh(pullRefreshState)) {
                        if (pullRefreshState.progress > 1.0f) {
                            PullRefreshIndicator(
                                backgroundColor = MaterialTheme.adminColors.backgroundPrimary,
                                refreshing = viewModel.isRefreshing,
                                state = pullRefreshState,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .zIndex(1f)
                            )
                        }
                        Column {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                itemsIndexed(
                                    items = viewModel.listings,
                                    key = { _, item -> item.listingId }
                                ) { index, item ->
                                    ListingItem(listing = item) {
                                        sharedViewModel?.selectedListing = item
                                        onListingTap()
                                    }
                                    if (index == viewModel.listings.size - 5) viewModel.loadNext()
                                }
                                if (viewModel.isLoadingNext) {
                                    item(span = { GridItemSpan(2) }) {
                                        LoadingNextIndicator()
                                    }
                                }
                            }
                        }
                        if (viewModel.isLoading) {
                            LoadingIndicator()
                        }
                    }
                }
            }
        }
    )
}

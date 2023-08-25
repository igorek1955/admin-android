package com.jarlingwar.adminapp.ui.screens.user_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.jarlingwar.adminapp.domain.models.UsersSortOrder
import com.jarlingwar.adminapp.ui.common.Drawer
import com.jarlingwar.adminapp.ui.common.DrawerItem
import com.jarlingwar.adminapp.ui.common.DrawerTopBar
import com.jarlingwar.adminapp.ui.common.DropDownTextMenu
import com.jarlingwar.adminapp.ui.common.LoadingIndicator
import com.jarlingwar.adminapp.ui.common.LoadingNextIndicator
import com.jarlingwar.adminapp.ui.common.NoResults
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.paddingPrimaryStartEnd
import com.jarlingwar.adminapp.ui.view_models.SharedViewModel
import com.jarlingwar.adminapp.ui.view_models.UsersViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UsersScreen(
    viewModel: UsersViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    onNavigate: (String) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(viewModel.isRefreshing, { viewModel.refresh() })
    LaunchedEffect(Unit) {
        viewModel.init()
    }
    val currentDestination = DrawerItem.USERS
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
                email = viewModel.currentUser?.email ?: "",
                imgUrl = viewModel.currentUser?.profileImageUrl ?: "",
                selectedItem = currentDestination,
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
                val sortOrder = UsersSortOrder.values().map { stringResource(id = it.titleResId) }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)) {
                    DropDownTextMenu(
                        Modifier.weight(0.5f), values = sortOrder, label = stringResource(
                            id = R.string.sorting
                        ),
                        selectedVal = stringResource(id = viewModel.params.orderBy.titleResId)
                    ) { viewModel.updateSortOrder(it) }
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
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(
                                items = viewModel.users,
                                key = { _, item -> item.userId }
                            ) { index, item ->
                                UserItem(user = item) {
                                    sharedViewModel.selectedUser = item
                                }
                                if (index == viewModel.users.size - 5) viewModel.loadNext()
                            }
                            if (viewModel.isLoadingNext) {
                                item(span = { GridItemSpan(2) }) {
                                    LoadingNextIndicator()
                                }
                            }
                        }
                        if (viewModel.isLoading && viewModel.users.isEmpty()) {
                            LoadingIndicator()
                        }
                    }
                }
            }
        }
    )
}
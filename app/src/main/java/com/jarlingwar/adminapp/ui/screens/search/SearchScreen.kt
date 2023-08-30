package com.jarlingwar.adminapp.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.ui.common.DrawerItem
import com.jarlingwar.adminapp.ui.common.DrawerScaffold
import com.jarlingwar.adminapp.ui.common.DropDownTextMenu
import com.jarlingwar.adminapp.ui.common.InputFieldIcon
import com.jarlingwar.adminapp.ui.common.LoadingIndicator
import com.jarlingwar.adminapp.ui.common.NoResults
import com.jarlingwar.adminapp.ui.common.showSnack
import com.jarlingwar.adminapp.ui.screens.listing_list.ListingItem
import com.jarlingwar.adminapp.ui.screens.user_list.UserItem
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.paddingPrimaryStartEnd
import com.jarlingwar.adminapp.ui.view_models.SearchViewModel
import com.jarlingwar.adminapp.ui.view_models.SharedViewModel
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.geo.capitalized

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    onNavigateToListing: () -> Unit,
    onNavigateToUser: () -> Unit,
    onNavigate: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.init()
    }
    var searchType by remember { mutableStateOf(SearchViewModel.SearchType.LISTINGS) }
    val searchQuery = remember { mutableStateOf("") }
    DrawerScaffold(
        currentUser = viewModel.currentUser,
        currentDestination = DrawerItem.SEARCH,
        onNavigate = onNavigate
    ) {
        LazyVerticalGrid(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .paddingPrimaryStartEnd()
        ) {

            item(span = { GridItemSpan(2) }) {
                ToggleRow(type = searchType, onToggle = { searchType = it })
            }

            item(span = { GridItemSpan(2) }) {
                SearchField(type = searchType, searchQuery = searchQuery, onSearch = { query, i ->
                    if (searchType == SearchViewModel.SearchType.LISTINGS) {
                        val field = SearchViewModel.ListingField.values()[i]
                        viewModel.searchListings(query, field)
                    } else {
                        val field = SearchViewModel.UserField.values()[i]
                        viewModel.searchUsers(query, field)
                    }
                })
            }

            if (viewModel.isNoResults && !viewModel.isLoading) {
                item(span = { GridItemSpan(2) }) { NoResults() }
            }

            if (viewModel.isLoading) {
                item(span = { GridItemSpan(2) }) {
                    LoadingIndicator()
                }
            }

            if (searchType == SearchViewModel.SearchType.LISTINGS) {
                items(items = viewModel.listings, key = { it.listingId }) { item ->
                    ListingItem(listing = item) {
                        sharedViewModel.selectedListing = item
                        onNavigateToListing()
                    }
                }
            } else {
                items(items = viewModel.users, key = { it.userId }) { item ->
                    UserItem(user = item) {
                        sharedViewModel.selectedUser = item
                        onNavigateToUser()
                    }
                }
            }
        }
        viewModel.error?.showSnack { viewModel.error = null }
    }
}

@Composable
private fun SearchField(
    type: SearchViewModel.SearchType,
    searchQuery: MutableState<String>,
    onSearch: (String, Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
            .background(MaterialTheme.adminColors.backgroundPrimary, RoundedCornerShape(5.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {

        var selectedFieldIndex by remember { mutableIntStateOf(0) }

        val searchByFieldList = if (type == SearchViewModel.SearchType.LISTINGS) {
            SearchViewModel.ListingField.values().map { it.name.lowercase() }
        } else {
            SearchViewModel.UserField.values().map { it.name.lowercase() }
        }

        LaunchedEffect(type) {
            selectedFieldIndex = 0
        }

        key(searchByFieldList) {
            DropDownTextMenu(
                Modifier
                    .weight(0.4f)
                    .zIndex(1f),
                backgroundColor = MaterialTheme.adminColors.backgroundPrimary,
                values = searchByFieldList,
                label = stringResource(R.string.search_by),
                defaultVal = searchByFieldList[selectedFieldIndex]
            ) {
                selectedFieldIndex = it
            }
        }

        TextField(
            modifier = Modifier.weight(0.6f),
            value = searchQuery.value,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(searchQuery.value, selectedFieldIndex)
                    focusManager.clearFocus()
                }
            ),
            onValueChange = { searchQuery.value = it },
            placeholder = { Text("Search ${type.name.lowercase()}", style = Type.Body1) },
            trailingIcon = {
                InputFieldIcon(
                    modifier = Modifier.clickable {
                        onSearch(searchQuery.value, selectedFieldIndex)
                        focusManager.clearFocus()
                    },
                    iconRes = R.drawable.ic_search_simple
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.adminColors.fillPrimary,
                unfocusedIndicatorColor = MaterialTheme.adminColors.fillAltSecondary,
                cursorColor = MaterialTheme.adminColors.primary
            )
        )
    }
}

@Composable
private fun ToggleRow(
    type: SearchViewModel.SearchType,
    onToggle: (SearchViewModel.SearchType) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = SearchViewModel.SearchType.LISTINGS.name.capitalized(),
            style = Type.Subtitle2, modifier = Modifier.padding(end = 5.dp)
        )
        Switch(checked = type == SearchViewModel.SearchType.LISTINGS,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.adminColors.primary
            ),
            onCheckedChange = {
                onToggle(SearchViewModel.SearchType.LISTINGS)
            })
        Text(
            text = SearchViewModel.SearchType.USERS.name.capitalized(),
            style = Type.Subtitle2, modifier = Modifier.padding(start = 20.dp, end = 5.dp)
        )
        Switch(checked = type == SearchViewModel.SearchType.USERS, colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.adminColors.primary
        ), onCheckedChange = {
            onToggle(SearchViewModel.SearchType.USERS)
        })
    }
}

@Preview(showBackground = true)
@Composable
private fun TogglePreview() {
    AdminAppTheme {
        var searchType by remember { mutableStateOf(SearchViewModel.SearchType.LISTINGS) }
        ToggleRow(type = searchType, onToggle = { searchType = it })
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchPreview() {
    AdminAppTheme {
        var searchType by remember { mutableStateOf(SearchViewModel.SearchType.LISTINGS) }
        var searchQuery = remember { mutableStateOf("") }

        Column(
            Modifier
                .fillMaxSize()
                .paddingPrimaryStartEnd()
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            SearchField(type = searchType, searchQuery = searchQuery, onSearch = { _, _ -> })
        }
    }
}
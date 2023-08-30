package com.jarlingwar.adminapp.ui.screens.reviews

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.domain.models.ReviewModel
import com.jarlingwar.adminapp.ui.common.DrawerItem
import com.jarlingwar.adminapp.ui.common.DrawerScaffold
import com.jarlingwar.adminapp.ui.common.LoadingIndicator
import com.jarlingwar.adminapp.ui.common.LoadingNextIndicator
import com.jarlingwar.adminapp.ui.common.NoResults
import com.jarlingwar.adminapp.ui.common.TopBar
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.paddingPrimaryStartEnd
import com.jarlingwar.adminapp.ui.view_models.ReviewsViewModel
import com.jarlingwar.adminapp.ui.view_models.SharedViewModel

@Composable
fun ReviewsScreen(
    viewModel: ReviewsViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    LaunchedEffect(Unit) { viewModel.init() }
    DrawerScaffold(
        currentUser = viewModel.currentUser,
        currentDestination = DrawerItem.REVIEWS,
        onNavigate = onNavigate
    ) {
        ReviewsBody(
            reviews = viewModel.reviews,
            isLoading = viewModel.isLoading,
            isLoadingNext = viewModel.isLoadingNext,
            isPaging = true,
            onModeSwitch = { viewModel.switchMode() },
            onLoadNext = { viewModel.loadNext() },
            onApprove = { viewModel.approve(it) },
            onReject = { viewModel.reject(it) }
        )
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UserReviewsScreen(
    viewModel: ReviewsViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.init(sharedViewModel.selectedUser) }
    Scaffold(
        topBar = {
            val title = "${sharedViewModel.selectedUser?.displayName}'s reviews"
            TopBar(title = title, onBack = onBack)
        }
    ) {
        ReviewsBody(
            reviews = viewModel.reviews,
            isLoading = false,
            isLoadingNext = false,
            isPaging = false,
            onModeSwitch = { },
            onLoadNext = { },
            onApprove = { viewModel.approve(it) },
            onReject = { viewModel.reject(it) }
        )
    }
}

@Composable
private fun ReviewsBody(
    reviews: List<ReviewModel>,
    isLoading: Boolean = false,
    isLoadingNext: Boolean = false,
    isPaging: Boolean = false,
    onModeSwitch: () -> Unit,
    onLoadNext: () -> Unit,
    onApprove: (ReviewModel) -> Unit = { },
    onReject: (ReviewModel) -> Unit = { }
) {
    var isPending by remember { mutableStateOf(false) }
    if (reviews.isEmpty() && !isLoading) {
        NoResults()
    } else {
        Column(
            Modifier
                .fillMaxWidth()
                .paddingPrimaryStartEnd()
        ) {

            if (isPaging) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.show_all_reviews),
                        style = Type.Subtitle2,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Switch(
                        checked = isPending,
                        onCheckedChange = {
                            isPending = !isPending
                            onModeSwitch()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.adminColors.primary
                        )
                    )
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                itemsIndexed(items = reviews, key = { _, item -> item.id }) { i, review ->
                    ReviewItem(
                        review = review,
                        onApprove = { onApprove(review) },
                        onReject = { onReject(review) }
                    )
                    if (isPaging && i == reviews.size - 5) onLoadNext()
                }
                if (isLoadingNext) {
                    item {
                        LoadingNextIndicator()
                    }
                }
            }
            if (isLoading) LoadingIndicator()
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ReviewsBodyPreview() {
    AdminAppTheme {
        val reviews = arrayListOf<ReviewModel>().apply {
            repeat(20) {
                add(ReviewModel.getMock())
            }
        }
        ReviewsBody(
            reviews = reviews,
            isLoading = false,
            isLoadingNext = true,
            isPaging = false,
            onModeSwitch = {},
            onLoadNext = {},
            onApprove = {},
            onReject = {}
        )
    }
}
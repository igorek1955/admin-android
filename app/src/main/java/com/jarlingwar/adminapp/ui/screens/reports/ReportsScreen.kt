package com.jarlingwar.adminapp.ui.screens.reports

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.domain.models.ReportModel
import com.jarlingwar.adminapp.ui.common.DrawerItem
import com.jarlingwar.adminapp.ui.common.DrawerScaffold
import com.jarlingwar.adminapp.ui.common.LoadingIndicator
import com.jarlingwar.adminapp.ui.common.LoadingNextIndicator
import com.jarlingwar.adminapp.ui.common.NoResults
import com.jarlingwar.adminapp.ui.common.showSnack
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.mainRoundedShape
import com.jarlingwar.adminapp.ui.theme.paddingPrimaryStartEnd
import com.jarlingwar.adminapp.ui.view_models.ReportsViewModel
import com.jarlingwar.adminapp.ui.view_models.SharedViewModel
import com.jarlingwar.adminapp.utils.getDateHyphen

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    onNavigateToListing: () -> Unit,
    onNavigateToUser: () -> Unit,
    onNavigate: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.init()
    }
    DrawerScaffold(
        currentUser = viewModel.currentUser,
        currentDestination = DrawerItem.REPORTS,
        onNavigate = onNavigate
    ) {
        val reports = viewModel.reports
        val isLoading = viewModel.isLoading
        if (reports.isEmpty() && !isLoading) {
            NoResults()
        } else {
            Column(
                Modifier
                    .fillMaxWidth()
                    .paddingPrimaryStartEnd()
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    itemsIndexed(items = reports, key = { _, item -> item.reportId }) { i, report ->
                        ReportItem(
                            reportModel = report,
                            onDelete = { viewModel.delete(report) },
                            onApprove = { viewModel.approve(report) },
                            onItemClick = {
                                if (report.isUser) {
                                    viewModel.getUser(report.reportedItemId)
                                } else if (report.isListing) {
                                    viewModel.getListing(report.reportedItemId)
                                }
                            }
                        )
                        if (i == reports.size - 5) viewModel.loadNext()
                    }
                    if (viewModel.isLoadingNext) {
                        item {
                            LoadingNextIndicator()
                        }
                    }
                }
                if (isLoading) LoadingIndicator()
            }
        }
        viewModel.error?.showSnack { viewModel.error = null }

        viewModel.reportedListing?.let {
            sharedViewModel.selectedListing = it
            onNavigateToListing()
            viewModel.reportedListing = null
        }

        viewModel.reportedUser?.let {
            sharedViewModel.selectedUser = it
            onNavigateToUser()
            viewModel.reportedUser = null
        }
    }
}

@Composable
private fun ReportItem(
    reportModel: ReportModel,
    onDelete: (ReportModel) -> Unit,
    onApprove: (ReportModel) -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        Modifier
            .padding(vertical = 3.dp)
            .heightIn(50.dp)
            .clickable { onItemClick() },
        shape = mainRoundedShape
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .paddingPrimaryStartEnd(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconRes = if (reportModel.isUser) R.drawable.ic_person else R.drawable.ic_details
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.adminColors.primary),
                modifier = Modifier.size(30.dp)
            )
            Column(
                Modifier
                    .paddingPrimaryStartEnd()
                    .widthIn(max = 230.dp)
            ) {
                Text(text = reportModel.reportReason, style = Type.Body1M)
                Text(text = getDateHyphen(reportModel.lastReported), style = Type.Body2)
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Row {
                    val iconColor =
                        ColorFilter.tint(if (!reportModel.processed) MaterialTheme.adminColors.fillAltPrimary else MaterialTheme.adminColors.fillTertiary)
                    IconButton(
                        onClick = { onApprove(reportModel) },
                        enabled = !reportModel.processed
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_thumb_up),
                            colorFilter = iconColor,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = { onDelete(reportModel) },
                        enabled = !reportModel.processed
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_thumb_down),
                            colorFilter = iconColor,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportPreview() {
    AdminAppTheme {
        val report = ReportModel.getMock()
        ReportItem(reportModel = report, onDelete = {}, onApprove = {}) {

        }
    }
}
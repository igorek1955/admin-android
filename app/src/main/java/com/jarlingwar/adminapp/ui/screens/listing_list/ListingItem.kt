package com.jarlingwar.adminapp.ui.screens.listing_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.ui.common.MyIcon
import com.jarlingwar.adminapp.ui.common.MyImage
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.FixedDimens
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.utils.geo.capitalized
import com.jarlingwar.adminapp.utils.geo.getCurrency
import com.jarlingwar.adminapp.utils.getDateListing

@Composable
fun ListingItem(listing: ListingModel, onItemClick: (ListingModel) -> Unit) {
    Column(
        Modifier
            .clip(RoundedCornerShape(FixedDimens.cornerRadius))
            .background(MaterialTheme.adminColors.backgroundSecondary)
            .height(205.dp)
            .clickable { onItemClick(listing) }
    ) {
        Box(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
        ) {
            MyImage(
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth(),
                imgUrl = listing.remoteImgUrlList.firstOrNull() ?: "",
                contentScale = ContentScale.Crop,
                shape = RoundedCornerShape(0.dp)
            )
            Column(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 2.dp, end = 2.dp)
                    .alpha(0.8f)
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.adminColors.backgroundSecondary)
                    .padding(horizontal = 5.dp),
                horizontalAlignment = Alignment.End
            ) {
                listing.location?.locationName?.let { loc ->
                    Text(
                        text = loc,
                        style = Type.Body2,
                        color = MaterialTheme.adminColors.textPrimary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MyIcon(R.drawable.ic_report)
                    Text(text = listing.reports.toString())
                    MyIcon(iconRes = R.drawable.ic_eye, paddingStart = 5.dp)
                    Text(text = listing.views.toString())
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        ) {
            Text(
                text = listing.category.toString(),
                style = Type.Body2, color = MaterialTheme.adminColors.textTertiary
            )
            Text(
                text = getDateListing(listing.created),
                style = Type.Body2, color = MaterialTheme.adminColors.textTertiary
            )
        }
        Text(
            text = listing.title, style = Type.Body1, modifier = Modifier.padding(start = 5.dp),
            maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        val priceStr = "${listing.price}${listing.location.getCurrency()}"
        Text(
            text = priceStr, style = Type.Body1M, modifier = Modifier.padding(start = 5.dp),
            maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun HorizontalListingItem(listing: ListingModel, onItemClick: (ListingModel) -> Unit) {
    Card(
        Modifier
            .height(105.dp)
            .clickable { onItemClick(listing) },
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box {
                MyImage(
                    modifier = Modifier.size(105.dp),
                    imgUrl = listing.remoteImgUrlList.firstOrNull() ?: "",
                    contentScale = ContentScale.Crop,
                    shape = RoundedCornerShape(FixedDimens.cornerRadius)
                )
                Column(Modifier.align(Alignment.BottomEnd).padding(5.dp)
                    .background(
                        MaterialTheme.adminColors.fixedSemiTransparent,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(3.dp)) {
                    Text(
                        text = listing.status.name.capitalized(),
                        style = Type.Body1,
                        color = Color.White
                    )
                    if (listing.approved) {
                        Text(text = stringResource(R.string.approved),  style = Type.Body3,
                            color = MaterialTheme.adminColors.primary)
                    } else {
                        Text(text = stringResource(R.string.not_approved),  style = Type.Body3,
                            color = MaterialTheme.adminColors.secondary )
                    }
                }
            }
            Row(
                Modifier
                    .padding(start = 5.dp, bottom = 5.dp, end = 5.dp)
                    .fillMaxWidth()) {
                Column(Modifier.weight(0.5f)) {
                    Text(
                        text = listing.title, style = Type.Body1,
                        maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                    val priceStr = "${listing.price}${listing.location.getCurrency()}"
                    Text(
                        text = priceStr, style = Type.Body2,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = listing.category.toString(),
                            style = Type.Body2, color = MaterialTheme.adminColors.textTertiary
                        )
                        Text(
                            text = getDateListing(listing.created),
                            style = Type.Body2, color = MaterialTheme.adminColors.textTertiary
                        )
                    }
                    listing.location?.locationName?.let { loc ->
                        Text(
                            text = loc,
                            style = Type.Body2,
                            color = MaterialTheme.adminColors.textPrimary,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MyIcon(R.drawable.ic_report)
                        Text(text = listing.reports.toString(), style = Type.Body2)
                        MyIcon(iconRes = R.drawable.ic_eye, paddingStart = 5.dp)
                        Text(text = listing.views.toString(), style = Type.Body2)
                    }
                }
                Text(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .weight(0.4f),
                    text = listing.description,
                    style = Type.Body3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListingItemPreview() {
    AdminAppTheme {
        val listing = ListingModel.getMock()
        ListingItem(listing = listing) { }
    }
}

@Preview(showBackground = true)
@Composable
private fun HorizontalListingItemPreview() {
    AdminAppTheme {
        val listing = ListingModel.getMock()
        HorizontalListingItem(listing = listing) { }
    }
}

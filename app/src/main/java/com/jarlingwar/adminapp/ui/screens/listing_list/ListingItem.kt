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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.domain.models.ListingModel
import com.jarlingwar.adminapp.ui.common.MyImage
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.adminDimens
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.geo.getCurrency
import com.jarlingwar.adminapp.utils.getTimeListing

@Composable
fun ListingItem(listing: ListingModel, onItemClick: (ListingModel) -> Unit) {
    Column(
        Modifier
            .clip(RoundedCornerShape(MaterialTheme.adminDimens.cornerRadius))
            .background(MaterialTheme.adminColors.backgroundSecondary)
            .height(205.dp)
            .clickable { onItemClick(listing) }
    ) {
        Box(modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()) {
            MyImage(
                modifier = Modifier.height(300.dp).fillMaxWidth(),
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
                    Icon(
                        modifier = Modifier.size(15.dp),
                        painter = painterResource(id = R.drawable.ic_report),
                        contentDescription = "reports"
                    )
                    Text(text = listing.reports.toString())
                    Icon(
                        modifier = Modifier.padding(start = 5.dp).size(15.dp),
                        painter = painterResource(id = R.drawable.ic_eye),
                        contentDescription = "reports"
                    )
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
                text = listing.category,
                style = Type.Body2, color = MaterialTheme.adminColors.textTertiary
            )
            Text(
                text = getTimeListing(listing.created),
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

@Preview(showBackground = true)
@Composable
private fun ListingItemPreview() {
    AdminAppTheme {
        val listing = ListingModel.getMock()
        ListingItem(listing = listing) { }
    }
}

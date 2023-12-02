package com.jarlingwar.adminapp.ui.screens.reviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.domain.models.ReviewModel
import com.jarlingwar.adminapp.ui.common.ExpandableText
import com.jarlingwar.adminapp.ui.common.MyImage
import com.jarlingwar.adminapp.ui.common.RevealDirection
import com.jarlingwar.adminapp.ui.common.SwipeCard
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.FixedDimens
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.utils.getDateHyphen
import kotlin.math.roundToInt



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReviewItem(review: ReviewModel, onApprove: () -> Unit, onReject: () -> Unit) {
    SwipeCard(
        modifier = Modifier
            .padding(vertical = 5.dp)
            .defaultMinSize(minHeight = FixedDimens.minReviewCardHeight),
        elevation = 3.dp,
        shape = RoundedCornerShape(FixedDimens.cornerRadius),
        directions = setOf(RevealDirection.StartToEnd, RevealDirection.EndToStart),
        backgroundCardEndColor = MaterialTheme.adminColors.backgroundPrimary,
        backgroundCardStartColor = MaterialTheme.adminColors.backgroundPrimary,
        hiddenContentStart = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(140.dp)
                    .background(
                        MaterialTheme.adminColors.primary,
                        RoundedCornerShape(
                            topStart = FixedDimens.cornerRadius,
                            bottomStart = FixedDimens.cornerRadius
                        )
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_thumb_up),
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(size = 28.dp),
                    contentDescription = "approve",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        },
        hiddenContentEnd = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(140.dp)
                    .background(
                        MaterialTheme.adminColors.secondary,
                        RoundedCornerShape(
                            bottomEnd = FixedDimens.cornerRadius,
                            topEnd = FixedDimens.cornerRadius
                        )
                    ),
                contentAlignment = Alignment.CenterEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_thumb_down),
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(size = 28.dp),
                    contentDescription = "reject",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        },
        onBackgroundStartClick = onApprove,
        onBackgroundEndClick = onReject,
    ) {
        Column(
            Modifier
                .clickable(false, onClick = {})
                .defaultMinSize(minHeight = FixedDimens.minReviewCardHeight)
                .background(
                    MaterialTheme.adminColors.backgroundPrimary,
                    RoundedCornerShape(FixedDimens.cornerRadius)
                )
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row {
                Column {
                    MyImage(
                        modifier = Modifier.size(70.dp),
                        imgUrl = review.reviewerImageUrl,
                        shape = CircleShape
                    )
                }
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(text = review.reviewerName, style = Type.Subtitle2M)
                    Text(
                        text = getDateHyphen(review.created),
                        style = Type.Body2,
                        color = MaterialTheme.adminColors.textSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(review.rating.roundToInt()) {
                            Image(
                                modifier = Modifier.size(15.dp),
                                painter = painterResource(id = R.drawable.ic_filled_star),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MaterialTheme.adminColors.primary)
                            )
                        }
                        Text(text = review.rating.toString(), style = Type.Body3)
                    }
                }
            }
            ExpandableText(
                modifier = Modifier.padding(top = 5.dp),
                text = review.body,
                style = Type.Body2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewItemPreview() {
    AdminAppTheme {
        val review = ReviewModel.getMock()
        Box(
            modifier = Modifier
                .background(MaterialTheme.adminColors.backgroundPrimary)
                .padding(10.dp)
        ) {
            ReviewItem(review, {}, {})
        }
    }
}
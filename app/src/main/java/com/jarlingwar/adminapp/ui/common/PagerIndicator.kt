package com.jarlingwar.adminapp.ui.common

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.paddingPrimaryStartEnd

@Composable
fun PagerIndicator(currentPage: Int, titles: List<String>, onChange: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .paddingPrimaryStartEnd()
            .background(MaterialTheme.adminColors.backgroundPrimary)
    ) {
        TabRow(
            modifier = Modifier.clip(RoundedCornerShape(10.dp)),
            backgroundColor = MaterialTheme.adminColors.backgroundSecondary,
            selectedTabIndex = currentPage,
            divider = { },
            indicator = { },
        ) {
            titles.forEachIndexed { index, title ->
                val isSelected = currentPage == index
                val bgColor = if (isSelected) MaterialTheme.adminColors.fillSecondary
                else Color.Transparent
                val textColor = if (isSelected) Color.White
                else MaterialTheme.adminColors.textPrimary
                Tab(modifier = Modifier.background(bgColor, RoundedCornerShape(10.dp)),
                    selected = isSelected,
                    onClick = { onChange(index) }) {
                    Text(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = title,
                        style = Type.Subtitle2,
                        color = textColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IndicatorLine(
    pageCount: Int,
    pagerState: PagerState,
) {
    Row(
        Modifier
            .height(24.dp)
            .padding(start = 4.dp)
            .fillMaxWidth(0.5f),
        horizontalArrangement = Arrangement.Start
    ) {
        repeat(pageCount) { iteration ->
            val lineWeight = animateFloatAsState(
                targetValue = if (pagerState.currentPage == iteration) {
                    1.5f
                } else {
                    if (iteration < pagerState.currentPage) 0.5f else 1f
                }, label = "size", animationSpec = tween(300, easing = EaseInOut)
            )
            val color =
                if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
                    .weight(lineWeight.value)
                    .height(4.dp)
            )
        }
    }
}


@Preview
@Composable
private fun PagerIndicatorPreview() {
    AdminAppTheme {
        val titles = listOf("Login", "Registration")
        PagerIndicator(currentPage = 0, titles = titles, onChange = { })
    }
}
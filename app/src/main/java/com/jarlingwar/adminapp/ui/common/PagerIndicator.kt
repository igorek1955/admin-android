package com.jarlingwar.adminapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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

@Preview
@Composable
fun PagerIndicatorPreview() {
    AdminAppTheme {
        val titles = listOf("Login", "Registration")
        PagerIndicator(currentPage = 0, titles = titles, onChange = { })
    }
}
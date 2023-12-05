package com.jarlingwar.adminapp.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors

@Composable
fun TopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    endContent: @Composable BoxScope.() -> Unit = { },
) {
    TopAppBar {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                onBack?.let {
                    IconButton(
                        onClick = { onBack() },
                        Modifier.padding(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "back",
                            alignment = Alignment.Center,
                            colorFilter = ColorFilter.tint(MaterialTheme.adminColors.fillPrimary)
                        )
                    }
                }
                Text(
                    text = title,
                    modifier = Modifier.padding(start = 5.dp),
                    style = Type.Subtitle2,
                    color = MaterialTheme.adminColors.textPrimary
                )
            }
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                endContent()
            }
        }
    }
}

@Composable
fun DrawerTopBar(title: String, onDrawerTap: () -> Unit) {
    TopAppBar {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = { onDrawerTap() },
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "menu",
                    alignment = Alignment.Center,
                    colorFilter = ColorFilter.tint(MaterialTheme.adminColors.fillPrimary)
                )
            }
            Text(
                text = title,
                modifier = Modifier.align(Alignment.Center),
                style = Type.Subtitle1,
                color = MaterialTheme.adminColors.textPrimary
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    AdminAppTheme {
        TopBar(title = "Registration", onBack = {}, endContent = {
            Image(painter = painterResource(id = R.drawable.ic_options), contentDescription = null)
        })
    }
}







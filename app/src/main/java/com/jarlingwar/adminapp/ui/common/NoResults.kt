package com.jarlingwar.adminapp.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type

@Composable
fun NoResults() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .padding(top = 30.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedImage(resId = R.raw.anim_error, size = 100.dp)
            Text(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(horizontal = 40.dp),
                text = "Нет результатов =(",
                style = Type.Subtitle2,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoResultsPreview() {
    AdminAppTheme {
        NoResults()
    }
}
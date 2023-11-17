package com.jarlingwar.adminapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.ui.theme.FixedDimens
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.paddingPrimaryStartEnd

@Composable
fun LogDialog(log: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.adminColors.backgroundAltPrimary)
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .paddingPrimaryStartEnd(),
            elevation = 5.dp,
            backgroundColor = MaterialTheme.adminColors.backgroundSecondary,
            shape = RoundedCornerShape(FixedDimens.cornerRadius)
        ) {
            Column(
                Modifier.padding(start = 10.dp, end = 30.dp, top = 10.dp, bottom = 10.dp),
            ) {
                Text(
                    text = log,
                    style = Type.Subtitle2,
                    color = MaterialTheme.adminColors.textPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = onDismiss) {
                    Text(text = "Dismiss")
                }
            }
        }
    }
}
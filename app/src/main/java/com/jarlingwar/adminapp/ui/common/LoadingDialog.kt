package com.jarlingwar.adminapp.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.adminDimens
import com.jarlingwar.adminapp.ui.theme.paddingPrimaryStartEnd

@Composable
fun LoadingDialog() {
    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .paddingPrimaryStartEnd(),
            elevation = 5.dp,
            backgroundColor = MaterialTheme.adminColors.backgroundSecondary,
            shape = RoundedCornerShape(MaterialTheme.adminDimens.cornerRadius)
        ) {
            Row(
                Modifier.padding(start = 10.dp, end = 30.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                AnimatedImage(resId = R.raw.anim_loading, 50.dp)
                Spacer(Modifier.width(10.dp))
                Text(text = stringResource(R.string.loading), style = Type.Subtitle2)
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedImage(resId = R.raw.anim_loading, 60.dp)
    }
}

@Composable
fun LoadingNextIndicator(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopCenter
) {
    Box(
        modifier = modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth(),
        contentAlignment = alignment
    ) {
        CircularProgressIndicator(color = MaterialTheme.adminColors.primary)
    }
}
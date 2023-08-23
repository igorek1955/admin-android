package com.jarlingwar.adminapp.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.paddingPrimaryStartEnd
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun MySnack(hostState: SnackbarHostState) {
    Box(Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = hostState,
            modifier = Modifier.align(Alignment.BottomCenter),
            snackbar = { data ->
                MySnackBody(text = data.message) {
                    hostState.currentSnackbarData?.dismiss()
                }
            })
    }
}

@Composable
fun MySnack(text: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        MySnackBody(text = text, onDismiss)
    }
    LaunchedEffect(text) {
        delay(3000)
        onDismiss()
    }
}

@Composable
fun AnimatedImage(resId: Int, size: Dp) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        restartOnPlay = false
    )
    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = Modifier.size(size)
    )
}

@Composable
private fun MySnackBody(text: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDismiss() }
            .paddingPrimaryStartEnd(),
        backgroundColor = MaterialTheme.adminColors.primary,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedImage(resId = R.raw.anim_swing, 40.dp)
            Spacer(Modifier.width(5.dp))
            Text(text = text, style = Type.Subtitle2)
        }
    }
}


@Preview
@Composable
private fun MySnackPreview() {
    AdminAppTheme {
        val hostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        MySnack(hostState = hostState)
        Box(modifier = Modifier.fillMaxSize()) {
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    hostState.showSnackbar("Howdy")
                }
            }
        }
    }
}
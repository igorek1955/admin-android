package com.jarlingwar.adminapp.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.adminDimens
import com.mxalbert.zoomable.Zoomable

@Composable
fun MyImage(
    modifier: Modifier,
    imgUrl: String,
    contentScale: ContentScale = ContentScale.Fit,
    shape: RoundedCornerShape = RoundedCornerShape(MaterialTheme.adminDimens.cornerRadius)
) {
    val placeholder = R.drawable.bg_image_placeholder
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(imgUrl)
                .diskCacheKey(imgUrl)
                .memoryCacheKey(imgUrl)
                .placeholder(placeholder)
                .error(R.drawable.img_unknown)
                .fallback(R.drawable.img_unknown)
                .size(Size.ORIGINAL)
                .build()
        ),
        modifier = modifier.clip(shape),
        contentDescription = "image",
        contentScale = contentScale
    )
}

@Composable
fun ImageDialog(
    imageUrl: String?,
    placeHolder: Int = R.drawable.bg_image_placeholder,
    onDismissAction: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissAction() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false
        )
    ) {
        Scaffold(
            Modifier
                .fillMaxSize()
                .background(Color.Black),
            topBar = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.Black),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onDismissAction() }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_clear),
                            colorFilter = ColorFilter.tint(MaterialTheme.adminColors.primary),
                            contentDescription = "close"
                        )
                    }
                }
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = padding.calculateBottomPadding())
                        .background(Color.Black),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(data = imageUrl)
                            .diskCacheKey(key = imageUrl)
                            .memoryCacheKey(key = imageUrl)
                            .placeholder(drawableResId = placeHolder)
                            .fallback(drawableResId = placeHolder)
                            .error(drawableResId = placeHolder)
                            .build()
                    )

                    if (painter.state is AsyncImagePainter.State.Loading) {
                        LoadingIndicator()
                    }
                    ZoomableImage(painter = painter) { onDismissAction() }
                }
            }
        )
    }
}

@Composable
fun MyIcon(iconRes: Int, tint: Color = MaterialTheme.adminColors.fillAltPrimary) {
    Icon(
        modifier = Modifier.size(15.dp),
        painter = painterResource(id = iconRes),
        tint = tint,
        contentDescription = null
    )
}

@Composable
fun ZoomableImage(painter: AsyncImagePainter, onDismiss: () -> Unit) {
    Zoomable(dismissGestureEnabled = true, onDismiss = { onDismiss(); true }) {
        val size = when (painter.state) {
            is AsyncImagePainter.State.Success -> painter.intrinsicSize
            else -> androidx.compose.ui.geometry.Size(1000f, 700f)
        }
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(size.width / size.height)
                .fillMaxSize()
        )
    }
}
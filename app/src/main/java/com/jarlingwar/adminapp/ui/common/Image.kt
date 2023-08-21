package com.jarlingwar.adminapp.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.ui.theme.adminDimens

@Composable
fun MyImage(
    imgUrl: String,
    size: Dp,
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
        modifier = Modifier.size(size).clip(shape),
        contentDescription = "image",
        contentScale = contentScale
    )
}
package com.jarlingwar.adminapp.ui.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp

class Dimens {
    val primaryStartEnd = 20.dp
    val buttonHeight = 60.dp
    val cornerRadius = 8.dp
}

fun Modifier.paddingPrimaryStartEnd() =
    composed { padding(horizontal = MaterialTheme.adminDimens.primaryStartEnd) }
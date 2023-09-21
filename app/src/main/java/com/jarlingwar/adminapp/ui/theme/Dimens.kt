package com.jarlingwar.adminapp.ui.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp

object FixedDimens {
    val cornerRadius = 10.dp
    val minReviewCardHeight = 140.dp
}

val mainRoundedShape = RoundedCornerShape(FixedDimens.cornerRadius)


abstract class Dimens {
    open var primaryStartEnd = 0.dp
    open var buttonHeight = 0.dp
}

object PhoneDimens: Dimens() {
    override var primaryStartEnd = 20.dp
    override var buttonHeight = 60.dp
}

fun Modifier.paddingPrimaryStartEnd() =
    composed { padding(horizontal = MaterialTheme.adminDimens.primaryStartEnd) }
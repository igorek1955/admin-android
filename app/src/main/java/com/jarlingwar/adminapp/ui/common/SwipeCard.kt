package com.jarlingwar.adminapp.ui.common

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ResistanceConfig
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
typealias RevealState = SwipeableState<RevealValue>

@ExperimentalMaterialApi
@Composable
fun SwipeCard(
    modifier: Modifier = Modifier,
    onContentClick: () -> Unit = {},
    onBackgroundStartClick: () -> Unit = {},
    onBackgroundEndClick: () -> Unit = {},
    elevation: Dp = 0.dp,
    shape: RoundedCornerShape = RoundedCornerShape(5.dp),
    maxRevealDp: Dp = 75.dp,
    directions: Set<RevealDirection> = setOf(
        RevealDirection.StartToEnd,
        RevealDirection.EndToStart
    ),
    backgroundCardColor: Color = MaterialTheme.colors.background,
    backgroundCardModifier: Modifier = modifier,
    backgroundCardStartColor: Color = MaterialTheme.colors.secondaryVariant,
    backgroundCardEndColor: Color = MaterialTheme.colors.secondary,
    backgroundCardContentColor: Color = MaterialTheme.colors.onSecondary,
    hiddenContentEnd: @Composable RowScope.() -> Unit = {},
    hiddenContentStart: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val state: RevealState = rememberRevealState()

    val contentClick = {
        if (state.targetValue != RevealValue.Default) {
            coroutineScope.launch {
                state.reset()
            }
            Unit
        } else onContentClick()
    }
    val backgroundStartClick = {
        if (state.targetValue == RevealValue.FullyRevealedEnd) {
            coroutineScope.launch {
                state.reset()
            }
        }
        onBackgroundStartClick()
    }

    val backgroundEndClick = {
        if (state.targetValue == RevealValue.FullyRevealedStart) {
            coroutineScope.launch {
                state.reset()
            }
        }
        onBackgroundEndClick()
    }

    Box {

        // alpha for background
        val maxRevealPx = with(LocalDensity.current) { maxRevealDp.toPx() }
        val draggedRatio =
            (state.offset.value.absoluteValue / maxRevealPx.absoluteValue).coerceIn(0f, 1f)

        // cubic parameters can be evaluated here https://cubic-bezier.com/
        val alpha = CubicBezierEasing(0.4f, 0.4f, 0.17f, 0.9f).transform(draggedRatio)

        val animatedBackgroundEndColor = if (alpha in 0f..1f) backgroundCardEndColor.copy(
            alpha = alpha
        ) else backgroundCardEndColor

        val animatedBackgroundStartColor = if (alpha in 0f..1f) backgroundCardStartColor.copy(
            alpha = alpha
        ) else backgroundCardStartColor

        Card(
            contentColor = backgroundCardContentColor,
            backgroundColor = backgroundCardColor,
            elevation = elevation,
            modifier = backgroundCardModifier
                .matchParentSize(),
            shape = shape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight()
                        .background(if (directions.contains(RevealDirection.StartToEnd)) animatedBackgroundStartColor else Color.Transparent)
                        .clickable(onClick = backgroundStartClick),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    content = hiddenContentStart
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight()
                        .background(if (directions.contains(RevealDirection.EndToStart)) animatedBackgroundEndColor else Color.Transparent)
                        .clickable(onClick = backgroundEndClick),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = hiddenContentEnd
                )
            }
        }

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onPrimary)
        {
            Box(
                modifier = modifier
                    .offset { IntOffset(state.offset.value.roundToInt(), 0) }
                    .revealSwipable(
                        state = state,
                        maxRevealPx = maxRevealPx,
                        directions = directions
                    )
                    .clickable { contentClick() }) { content() }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun Modifier.revealSwipable(
    maxRevealPx: Float,
    directions: Set<RevealDirection>,
    state: RevealState,
) = composed {
    val maxAmountOfOverflowPx = with(LocalDensity.current) { 250.dp.toPx() }
    val anchors = mutableMapOf(0f to RevealValue.Default)
    if (RevealDirection.StartToEnd in directions) anchors += maxRevealPx to RevealValue.FullyRevealedEnd
    if (RevealDirection.EndToStart in directions) anchors += -maxRevealPx to RevealValue.FullyRevealedStart
    val thresholds = { _: RevealValue, _: RevealValue -> FractionalThreshold(0.5f) }

    val minFactor =
        if (RevealDirection.EndToStart in directions) SwipeableDefaults.StandardResistanceFactor else SwipeableDefaults.StiffResistanceFactor
    val maxFactor =
        if (RevealDirection.StartToEnd in directions) SwipeableDefaults.StandardResistanceFactor else SwipeableDefaults.StiffResistanceFactor

    Modifier.swipeable(
        state = state,
        anchors = anchors,
        thresholds = thresholds,
        orientation = Orientation.Horizontal,
        resistance = ResistanceConfig(
            basis = maxAmountOfOverflowPx,
            factorAtMin = minFactor,
            factorAtMax = maxFactor
        )
    )
}

enum class RevealDirection {
    StartToEnd,
    EndToStart
}

enum class RevealValue {
    Default,
    FullyRevealedEnd,
    FullyRevealedStart,
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberRevealState(
    initialValue: RevealValue = RevealValue.Default,
    confirmStateChange: (RevealValue) -> Boolean = { true },
): RevealState {
    return rememberSwipeableState(
        initialValue = initialValue,
        confirmStateChange = confirmStateChange
    )
}

@ExperimentalMaterialApi
suspend fun RevealState.reset() { animateTo(targetValue = RevealValue.Default) }
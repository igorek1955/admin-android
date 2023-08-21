package com.jarlingwar.adminapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

val MaterialTheme.adminColors: ColorPalette
    @Composable
    @ReadOnlyComposable
    get() = if (!this.colors.isLight) DarkPalette else LightPalette

val MaterialTheme.adminDimens: Dimens
    @Composable
    @ReadOnlyComposable
    get() = Dimens()


@Composable
fun AdminAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val palette = if (darkTheme) DarkPalette else LightPalette
    systemUiController.setSystemBarsColor(
        color = palette.backgroundPrimary
    )
    MaterialTheme(
        colors = palette.material,
        typography = MyTypography,
        content = content
    )
}
package com.jarlingwar.adminapp.ui.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

abstract class ColorPalette {

    // Fixed Colors
    val primary = Color(0xFF5FAD56)
    val secondary = Color(0xFFF78154)
    val tertiary = Color(0xFF7F9873)
    val dangerPrimary = Color(0xFFFF3F3F)
    val fixedSemiTransparent = Color(0x8B323030)

    open val textPrimary = Color(0xFF090909)
    open val textSecondary = Color(0xFF6B6969)
    open val textTertiary = Color(0x9A000000)
    open val textAltPrimary = Color(0xFFFFFFFF)
    open val textAltSecondary = Color(0xE9FFFFFF)
    open val fillPrimary = primary
    open val fillSecondary = secondary
    open val fillTertiary = Color(0xCEA3A3A3)
    open val fillAltPrimary = Color(0x8F000000)
    open val fillAltSecondary = Color(0x1C000000)
    open val strokePrimary = Color(0x1C000000)
    open val strokeSecondary = Color(0x14000000)
    open val strokeAltPrimary = Color(0x1CFFFFFF)
    open val strokeAltQuaternary = Color(0x09FFFFFF)
    open val backgroundPrimary = Color(0xFFFFFFFF)
    open val backgroundTertiary = Color(0x4CFFFFFF)
    open val backgroundSecondary = Color(0x65F1F1F1)
    open val backgroundAltPrimary = Color(0xFF1C1C1C)
    open val backgroundAltSecondary = Color(0xFF1C1C1C)

    abstract val material: Colors
}

object LightPalette : ColorPalette() {
    override val material: Colors
        get() = Colors(
            isLight = true,
            primary = backgroundPrimary,
            secondary = backgroundPrimary,
            background = backgroundPrimary,
            error = dangerPrimary,
            surface = backgroundPrimary,
            onBackground = textPrimary,
            onError = textPrimary,
            onPrimary = textPrimary,
            onSecondary = textPrimary,
            onSurface = textPrimary,
            primaryVariant = textAltPrimary,
            secondaryVariant = textAltSecondary
        )
}

object DarkPalette : ColorPalette() {
    override val textPrimary = Color(0xFFFFFFFF)
    override val textSecondary = Color(0xE8C6BFBF)
    override val textAltPrimary = Color(0xFF1C1C1C)
    override val textAltSecondary = Color(0xFF1C1C1C)
    override val textTertiary = Color(0xA1F0E9E9)
    override val fillAltPrimary = Color(0xC5F1ECEC)
    override val fillAltSecondary = Color(0xFFFFFFFF)
    override val fillTertiary: Color = Color(0x48323232)
    override val strokePrimary = Color(0x1CFFFFFF)
    override val strokeSecondary = Color(0x13FFFFFF)
    override val strokeAltPrimary = Color(0x1C000000)
    override val strokeAltQuaternary = Color(0x07000000)
    override val backgroundPrimary = Color(0xFF1C1C1C)
    override val backgroundSecondary = Color(0x88252525)
    override val backgroundTertiary = Color(0x87252222)
    override val backgroundAltPrimary = Color(0x6DFAF9F9)
    override val backgroundAltSecondary = Color(0xFFF8F8F8)
    override val material: Colors
        get() = Colors(
            isLight = false,
            primary = LightPalette.backgroundPrimary,
            secondary = LightPalette.backgroundPrimary,
            background = backgroundPrimary,
            error = dangerPrimary,
            surface = backgroundPrimary,
            onBackground = textPrimary,
            onError = textPrimary,
            onPrimary = textPrimary,
            onSecondary = textPrimary,
            onSurface = textPrimary,
            primaryVariant = textAltPrimary,
            secondaryVariant = textAltSecondary
        )
}
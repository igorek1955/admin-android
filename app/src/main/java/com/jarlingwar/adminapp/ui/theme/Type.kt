package com.jarlingwar.adminapp.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val ChosenFont = FontFamily.SansSerif

val MyTypography = Typography(
    h1 = Type.Header1,
    h2 = Type.Header2,
    subtitle1 = Type.Subtitle1,
    subtitle2 = Type.Subtitle2,
    body1 = Type.Body1,
    body2 = Type.Body2
)

object Type {
    val Header1 = TextStyle(
        fontFamily = ChosenFont,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp
    )
    val Header2 = TextStyle(
        fontFamily = ChosenFont,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 28.sp
    )
    val Subtitle1 = TextStyle(
        fontFamily = ChosenFont,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 22.sp
    )
    val Subtitle1M = TextStyle(
        fontFamily = ChosenFont,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 22.sp
    )
    val Subtitle2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
    val Subtitle2M = TextStyle(
        fontFamily = ChosenFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
    val Body1 = TextStyle(
        fontFamily = ChosenFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
    val Body1M = TextStyle(
        fontFamily = ChosenFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
    val Body2 = TextStyle(
        fontFamily = ChosenFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    val Body3 = TextStyle(
        fontFamily = ChosenFont,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
}
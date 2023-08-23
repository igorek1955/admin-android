package com.jarlingwar.adminapp.ui.common

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.adminDimens

@Composable
fun PrimaryButton(
    text: String,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(MaterialTheme.adminDimens.buttonHeight),
    isEnabled: Boolean = true,
    onTap: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.adminColors.primary
        ),
        onClick = { onTap() }) {
        Text(text = text, style = Type.Subtitle2, color = Color.White)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(MaterialTheme.adminDimens.buttonHeight),
    isEnabled: Boolean = true,
    onTap: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.adminColors.secondary
        ),
        onClick = { onTap() }) {
        Text(text = text, style = Type.Subtitle2, color = Color.White)
    }
}

@Composable
fun NeutralButton(
    text: String,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(MaterialTheme.adminDimens.buttonHeight),
    isEnabled: Boolean = true,
    onTap: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        onClick = { onTap() }) {
        Text(text = text, style = Type.Subtitle2, color = MaterialTheme.adminColors.textPrimary)
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    AdminAppTheme {
        PrimaryButton(text = "Primary Button") { }
    }
}

@Preview
@Composable
private fun SecondaryButtonPreview() {
    AdminAppTheme {
        SecondaryButton(text = "Secondary Button") { }
    }
}

@Preview
@Composable
private fun NeutralButtonPreview() {
    AdminAppTheme {
        NeutralButton(text = "Secondary Button") { }
    }
}
package com.jarlingwar.adminapp.ui.common

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
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.adminDimens

@Composable
fun PrimaryButton(text: String, isEnabled: Boolean = true, onTap: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.adminDimens.buttonHeight),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.adminColors.primary
        ),
        onClick = { onTap() }) {
        Text(text = text, style = Type.Subtitle2, color = Color.White)
    }
}

@Composable
fun SecondaryButton(text: String, isEnabled: Boolean = true, onTap: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.adminDimens.buttonHeight),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.adminColors.secondary
        ),
        onClick = { onTap() }) {
        Text(text = text, style = Type.Subtitle2, color = Color.White)
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    AdminAppTheme() {
        PrimaryButton("Primary Button") { }
    }
}

@Preview
@Composable
private fun SecondaryButtonPreview() {
    AdminAppTheme() {
        SecondaryButton("Secondary Button") { }
    }
}
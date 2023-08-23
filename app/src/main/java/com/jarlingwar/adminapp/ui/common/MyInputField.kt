package com.jarlingwar.adminapp.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors


@Composable
fun MyInputField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    textVal: MutableState<String>,
    label: String? = null,
    placeholder: String,
    errorText: String = "",
    singleLine: Boolean = true,
    startIcon: Int? = null,
    isClearEnabled: Boolean = true
) {
    Column {
        OutlinedTextField(
            modifier = modifier,
            value = textVal.value,
            singleLine = singleLine,
            isError = errorText.isNotEmpty(),
            leadingIcon = startIcon?.let { { InputFieldIcon(iconRes = it) } },
            trailingIcon = if (isClearEnabled) {
                {
                    if (textVal.value.isNotEmpty()) {
                        InputFieldIcon(
                            modifier = Modifier.clickable { textVal.value = "" },
                            iconRes = R.drawable.ic_clear
                        )
                    }
                }
            } else null,
            onValueChange = { newVal -> textVal.value = newVal },
            label = label?.let { { Text(text = it) } },
            placeholder = { Text(placeholder) },
            colors = myTextFieldDefaults()
        )
        Box(modifier = Modifier.height(20.dp)) {
            if (errorText.isNotEmpty()) {
                Text(errorText, style = Type.Body2, color = MaterialTheme.adminColors.dangerPrimary)
            }
        }
    }
}

@Composable
fun MyPasswordField(
    textVal: MutableState<String>,
    label: String,
    errorText: String = "",
    placeholder: String
) {
    var isPassVisible by rememberSaveable { mutableStateOf(false) }
    Column {
        OutlinedTextField(
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            value = textVal.value,
            isError = errorText.isNotEmpty(),
            visualTransformation = if (isPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { InputFieldIcon(iconRes = R.drawable.ic_lock) },
            trailingIcon = {
                if (textVal.value.isNotEmpty()) {
                    InputFieldIcon(
                        modifier = Modifier.clickable { isPassVisible = !isPassVisible },
                        iconRes = R.drawable.ic_eye
                    )
                }
            },
            onValueChange = { newVal -> textVal.value = newVal },
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            colors = myTextFieldDefaults()
        )
        Box(modifier = Modifier.height(20.dp)) {
            if (errorText.isNotEmpty()) {
                Text(errorText, style = Type.Body2, color = MaterialTheme.adminColors.dangerPrimary)
            }
        }
    }
}

@Composable
private fun myTextFieldDefaults() = TextFieldDefaults.textFieldColors(
    backgroundColor = MaterialTheme.adminColors.backgroundPrimary,
    cursorColor = MaterialTheme.adminColors.primary,
    focusedIndicatorColor = MaterialTheme.adminColors.primary,
    unfocusedLabelColor = MaterialTheme.adminColors.textTertiary,
    focusedLabelColor = MaterialTheme.adminColors.primary
)

@Composable
private fun InputFieldIcon(modifier: Modifier = Modifier, iconRes: Int) {
    Image(
        modifier = modifier.size(24.dp),
        painter = painterResource(id = iconRes),
        colorFilter = ColorFilter.tint(MaterialTheme.adminColors.primary),
        contentDescription = "icon"
    )
}
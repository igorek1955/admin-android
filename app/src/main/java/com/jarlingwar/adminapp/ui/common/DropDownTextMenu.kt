package com.jarlingwar.adminapp.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.adminColors

/**
 * @param selectedVal use outside value selector if not null
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun DropDownTextMenu(
    modifier: Modifier = Modifier,
    leadingIconRes: Int? = null,
    defaultVal: String? = null,
    backgroundColor: Color = MaterialTheme.adminColors.backgroundSecondary,
    label: String,
    values: List<String>,
    onValueChange: (Int) -> Unit
) {
    var selectedItem by remember {
        defaultVal?.let { mutableStateOf(it) } ?: mutableStateOf(values.first())
    }
    var isExpanded by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    Column(modifier) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            modifier = Modifier.onFocusChanged { keyboard?.hide() },
            onExpandedChange = { isExpanded = it }) {
            TextField(
                value = selectedItem,
                readOnly = true,
                onValueChange = { },
                modifier = Modifier.focusable(false),
                leadingIcon = if (leadingIconRes != null) {
                    {
                        Image(
                            painterResource(id = leadingIconRes),
                            contentDescription = ""
                        )
                    }
                } else null,
                label = { Text(label) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = backgroundColor,
                    trailingIconColor = MaterialTheme.adminColors.primary,
                    focusedLabelColor = MaterialTheme.adminColors.textSecondary,
                    unfocusedLabelColor = MaterialTheme.adminColors.textSecondary,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                }
            )
            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                values.forEachIndexed { index, label ->
                    DropdownMenuItem(onClick = {
                        selectedItem = label
                        isExpanded = false
                        onValueChange(index)
                    }) {
                        Text(text = label)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DropDownPreview() {
    AdminAppTheme {
        val values = listOf("Moscow", "New York", "Las Vegas", "Los Angeles", "Beijing")
        Column(Modifier.fillMaxSize()) {
            DropDownTextMenu(values = values, label = "Сортировка") { }
        }
    }
}

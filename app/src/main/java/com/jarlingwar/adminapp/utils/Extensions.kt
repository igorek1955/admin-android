package com.jarlingwar.adminapp.utils

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

fun <T> ComponentActivity.observeAndAction(flow: MutableStateFlow<T>, action: (T) -> Unit) {
    lifecycleScope.launch { flow.collect { action(it) } }
}
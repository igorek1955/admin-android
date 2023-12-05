package com.jarlingwar.adminapp.utils

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat

fun <T> ComponentActivity.observeAndAction(flow: MutableStateFlow<T>, action: (T) -> Unit) {
    lifecycleScope.launch { flow.collect { action(it) } }
}

fun Double.round(decimalPlaces: Int = 2): String {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN
    return df.format(this)
}

fun <T> T.prettyPrint(): String {
    val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
    return gsonBuilder.toJson(this)
}

fun Intent.clear() {
    action = null
    data = null
    replaceExtras(null)
    flags = 0
}
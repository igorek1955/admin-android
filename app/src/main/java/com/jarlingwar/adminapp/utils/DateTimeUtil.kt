package com.jarlingwar.adminapp.utils

import com.jarlingwar.adminapp.utils.LocaleHelper.getCurrentLocale
import java.text.SimpleDateFormat
import java.util.*

fun getTimeTitle(): String {
    return SimpleDateFormat("ssmmhhddMMyyyy", Locale.ENGLISH).format(Date())
}

fun getTimeHyphen(time: Long): String {
    return SimpleDateFormat("dd-MMM-yyyy", getCurrentLocale()).format(Date(time))
}

fun getTimeListing(time: Long): String {
    return SimpleDateFormat("dd/MMM/yyyy", getCurrentLocale()).format(Date(time))
}

fun getTimeMessage(time: Long): String {
    return SimpleDateFormat("hh:mm", getCurrentLocale()).format(Date(time))
}

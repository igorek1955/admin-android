package com.jarlingwar.adminapp.utils.geo


fun String.safeSubstring(endIndex: Int): String {
    return if (this.length > endIndex) {
        this.substring(0, endIndex)
    } else this
}
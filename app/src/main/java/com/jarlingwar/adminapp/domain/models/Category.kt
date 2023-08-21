package com.jarlingwar.adminapp.domain.models

import com.jarlingwar.adminapp.R

enum class Category(val type: String, val titleResId: Int) {
    SERVICE("service", R.string.service),
    EXCHANGE("exchange", R.string.exchange),
    USED("used", R.string.used),
    RESTAURANT("food", R.string.food),
    PLACE("place", R.string.place),
    ELECTRONICS("electronics", R.string.electronics),
    CLOTHES("clothes", R.string.clothes),
    NEW("new", R.string.new_things)
}

fun getCategoryByType(type: String?): Category? {
    return Category.values().firstOrNull { it.type == type }
}
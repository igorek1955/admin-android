package com.jarlingwar.adminapp.domain.models

import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.utils.geo.capitalized

enum class Category {
    SERVICE,
    EXCHANGE,
    USED,
    FOOD,
    PLACE,
    ELECTRONICS,
    CLOTHES,
    NEW;

    override fun toString(): String {
       return this.name.capitalized()
    }
}

fun Category.getResId(): Int {
    return when (this) {
        Category.SERVICE -> R.string.service
        Category.EXCHANGE -> R.string.exchange
        Category.USED -> R.string.used
        Category.FOOD -> R.string.food
        Category.PLACE -> R.string.place
        Category.ELECTRONICS -> R.string.electronics
        Category.CLOTHES -> R.string.clothes
        Category.NEW -> R.string.new_things
    }
}

package com.jarlingwar.adminapp.domain.models

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

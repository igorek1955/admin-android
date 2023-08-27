package com.jarlingwar.adminapp.domain.models

data class BlockedUser(
    val userId: String = "",
    var email: String = "",
    var created: Long = 0
)
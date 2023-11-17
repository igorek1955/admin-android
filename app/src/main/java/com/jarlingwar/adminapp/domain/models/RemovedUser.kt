package com.jarlingwar.adminapp.domain.models
/**
 * deleted by user himself or blocked user
 */
data class RemovedUser(
    val userId: String = "",
    var email: String = "",
    var created: Long = 0,
    var isBlocked: Boolean = false,
    var isSelfDestruct: Boolean = false
)
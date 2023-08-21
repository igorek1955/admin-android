package com.jarlingwar.adminapp.domain.models

import com.jarlingwar.adminapp.R

data class ReportModel(
    var reportedItemId: String = "",
    var reportReasons: List<Int> = emptyList(),
    var isListing: Boolean = false,
    var isUser: Boolean = false,
    var lastReported: Long = 0)

enum class ReportReason(val reasonId: Int, val resId: Int, val localId: Int) {
    HARASSMENT(0, R.string.reason_harassment, 10000),
    SEXUAL(1, R.string.reason_sexual, 10001),
    RUDE(2, R.string.reason_rude, 10002),
    FAKE(3, R.string.reason_fake, 10003),
    SCAM(4, R.string.reason_scam, 10004),
    INAPPROPRIATE(5, R.string.reason_inappropriate, 10005)
}

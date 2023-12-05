package com.jarlingwar.adminapp.domain.models

import com.jarlingwar.adminapp.R
import java.util.Random
import java.util.UUID

data class ReportModel(
    var reportedItemId: String = "",
    var reportAuthorId: String = "",
    var reportId: String = "",
    var reportReason: String = "",
    var isListing: Boolean = false,
    var isUser: Boolean = false,
    var processed: Boolean = false,
    var lastReported: Long = 0
) {
    companion object {
        fun getMock(): ReportModel {
            val rand = Random()
            val reportReason = ReportReason.values()[rand.nextInt(ReportReason.values().lastIndex)]
            return ReportModel(
                reportedItemId = UUID.randomUUID().toString(),
                reportAuthorId = UUID.randomUUID().toString(),
                reportId = UUID.randomUUID().toString(),
                reportReason = reportReason.name,
                isListing = rand.nextBoolean(),
                isUser = rand.nextBoolean(),
                processed = rand.nextBoolean(),
                lastReported = System.currentTimeMillis()
            )
        }
    }
}

enum class ReportReason(val resId: Int, val localId: Int) {
    HARASSMENT(R.string.reason_harassment, 10000),
    SEXUAL(R.string.reason_sexual, 10001),
    RUDE(R.string.reason_rude, 10002),
    FAKE(R.string.reason_fake, 10003),
    SCAM(R.string.reason_scam, 10004),
    INAPPROPRIATE(R.string.reason_inappropriate, 10005),
    COPYRIGHTS(R.string.reason_copyrights, 10006),
    OTHER(R.string.other, 10007)
}

package com.jarlingwar.adminapp.domain.repositories.remote

import com.jarlingwar.adminapp.domain.models.ReportModel

interface IReportRepository {
    suspend fun submitReport(report: ReportModel): Result<Unit>
}
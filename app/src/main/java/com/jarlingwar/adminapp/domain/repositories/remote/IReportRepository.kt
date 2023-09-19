package com.jarlingwar.adminapp.domain.repositories.remote

import com.jarlingwar.adminapp.domain.models.ReportModel
import kotlinx.coroutines.flow.Flow

interface IReportRepository {
    fun getReportsPaging(pagingReference: Flow<Int>): Flow<List<ReportModel>>
    suspend fun update(report: ReportModel): Result<Unit>
    suspend fun delete(report: ReportModel): Result<Unit>
}
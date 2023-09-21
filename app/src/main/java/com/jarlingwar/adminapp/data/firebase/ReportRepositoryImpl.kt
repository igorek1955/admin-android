package com.jarlingwar.adminapp.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jarlingwar.adminapp.domain.models.ReportModel
import com.jarlingwar.adminapp.domain.repositories.remote.IReportRepository
import com.jarlingwar.adminapp.utils.FirestoreCollections
import com.jarlingwar.adminapp.utils.ReportFields
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.ReviewFields
import com.jarlingwar.adminapp.utils.paginate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ReportRepositoryImpl(private val db: FirebaseFirestore): IReportRepository {
    private val reports = db.collection(FirestoreCollections.REPORTS)
    override fun getReportsPaging(pagingReference: Flow<Int>): Flow<List<ReportModel>> {
        return reports
            .orderBy(ReportFields.LAST_REPORTED, Query.Direction.DESCENDING)
            .paginate(pagingReference, 50)
            .map { docs -> docs.mapNotNull { it.toObject(ReportModel::class.java) } }
    }

    override suspend fun update(report: ReportModel): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                reports
                    .document(report.reportId)
                    .set(report)
                    .await()
                Result.success(Unit)
            } catch (e: Exception) {
                ReportHandler.reportError(e)
                Result.failure(e)
            }
        }
    }


    override suspend fun delete(report: ReportModel): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                reports
                    .document(report.reportId)
                    .delete()
                    .await()
                Result.success(Unit)
            } catch (e: Exception) {
                ReportHandler.reportError(e)
                Result.failure(e)
            }
        }
    }
}
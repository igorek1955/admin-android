package com.jarlingwar.adminapp.utils

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


fun Query.paginate(pagingStartVal: Flow<Int>, limit: Long): Flow<List<DocumentSnapshot>> = flow {
    val documents = mutableListOf<DocumentSnapshot>()
    documents.addAll(
        suspendCoroutine { continuation ->
            this@paginate.limit(limit).get()
                .addOnSuccessListener { continuation.resume(it.documents) }
                .addOnFailureListener {
                    ReportHandler.reportError(it)
                    continuation.resumeWithException(it)
                }
        }
    )
    emit(documents)
    pagingStartVal.transform { value ->
        if (value == documents.size && documents.size > 0) {
            documents.addAll(
                suspendCoroutine { c ->
                    this@paginate.startAfter(documents.last())
                        .limit(limit)
                        .get()
                        .addOnSuccessListener {
                            c.resume(it.documents)
                        }
                        .addOnFailureListener {
                            ReportHandler.reportError(it)
                            c.resumeWithException(it)
                        }
                }
            )
            emit(documents)
        }
    }.collect { docs ->
        emit(docs)
    }
}
package com.jarlingwar.adminapp.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.jarlingwar.adminapp.domain.repositories.remote.IChatRepository
import com.jarlingwar.adminapp.domain.repositories.remote.IReviewRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MonitoringService: Service() {
    @Inject
    lateinit var chatRepo: IChatRepository
    @Inject
    lateinit var reviewsRepo: IReviewRepository
//    @Inject
//    lateinit var listingsRepo: ListingManager
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
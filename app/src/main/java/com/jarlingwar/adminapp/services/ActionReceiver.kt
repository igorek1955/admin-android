package com.jarlingwar.adminapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jarlingwar.adminapp.domain.repositories.remote.INewListingsRepository
import com.jarlingwar.adminapp.domain.repositories.remote.IReviewRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var newListingsRepo: INewListingsRepository

    @Inject
    lateinit var reviewsRepo: IReviewRepository

    companion object {
        const val ITEM_ID = "itemId"
        const val ACTION_APPROVE_LISTING = "ACTION_APPROVE_LISTING"
        const val ACTION_APPROVE_REVIEW = "ACTION_APPROVE_REVIEW"
        const val ACTION_REJECT_REVIEW = "ACTION_REJECT_REVIEW"
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override fun onReceive(ctx: Context?, intent: Intent?) {
        intent?.action?.let { action ->
            val itemId = intent.getStringExtra(ITEM_ID)
            when (action) {
                ACTION_APPROVE_LISTING -> {
                    if (itemId != null) {
                        coroutineScope.launch { newListingsRepo.approveListing(itemId) }
                    }
                }

                ACTION_REJECT_REVIEW -> {
                    if (itemId != null) {
                        coroutineScope.launch { reviewsRepo.deleteReview(itemId) }
                    }
                }

                ACTION_APPROVE_REVIEW -> {
                    if (itemId != null) {
                        coroutineScope.launch { reviewsRepo.approveReview(itemId) }
                    }
                }

                else -> {}
            }
        }
    }

}
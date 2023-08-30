package com.jarlingwar.adminapp.ui.view_models

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jarlingwar.adminapp.domain.UserManager
import com.jarlingwar.adminapp.domain.models.ReviewModel
import com.jarlingwar.adminapp.domain.models.UserModel
import com.jarlingwar.adminapp.domain.repositories.remote.IReviewRepository
import com.jarlingwar.adminapp.utils.AbstractPager
import com.jarlingwar.adminapp.utils.CustomError
import com.jarlingwar.adminapp.utils.Pager
import com.jarlingwar.adminapp.utils.ReportHandler
import com.jarlingwar.adminapp.utils.toUnknown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    application: Application,
    private val reviewsRepo: IReviewRepository,
    private val userManager: UserManager
) : AndroidViewModel(application) {

    private var user: UserModel? = null
    var reviews by mutableStateOf<List<ReviewModel>>(emptyList())
    var error by mutableStateOf<CustomError?>(null)
    var isLoading by mutableStateOf(false)
    var isLoadingNext by mutableStateOf(false)
    var isRefreshing by mutableStateOf(false)
    var currentUser by mutableStateOf<UserModel?>(null)
    var showOnlyPendingReviews by mutableStateOf(false)
    private var pager: Pager<ReviewModel>? = null

    fun init(user: UserModel? = null, isPending: Boolean = false) {
        this.user = user
        isLoading = true
        showOnlyPendingReviews = isPending
        viewModelScope.launch(Dispatchers.IO) {
            userManager.userInfoFlow.collectLatest { currentUser = it }
        }
        if (user != null) getUserReviews()
        else  startPaging()
    }

    fun approve(review: ReviewModel) {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            review.approved = true
            reviewsRepo.updateReview(review)
                .onSuccess {
                    val updatedList = reviews.filterNot { it.id == review.id }
                    reviews = updatedList
                    isLoading = false
                }
                .onFailure {
                    isLoading = false
                    error = it.toUnknown()
                }
        }
    }

    fun reject(review: ReviewModel) {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            reviewsRepo.deleteReviews(listOf(review.id))
                .onSuccess {
                    val updatedList = reviews.filterNot { it.id == review.id }
                    reviews = updatedList
                    isLoading = false
                }
                .onFailure {
                    isLoading = false
                    error = it.toUnknown()
                }
        }
    }

    fun loadNext() {
        pager?.loadNext()
    }

    fun switchMode() {
        isLoading = true
        showOnlyPendingReviews = !showOnlyPendingReviews
        pager?.stop()
        startPaging()
    }

    private fun getUserReviews() {
       viewModelScope.launch(Dispatchers.IO) {
           reviewsRepo.getReviewsByUserId(user!!.userId)
               .onSuccess {
                   isLoading = false
                   reviews = it
               }
               .onFailure {
                   isLoading = false
                   error = it.toUnknown()
               }
       }
    }

    private fun startPaging() {
        if (pager == null) {
            setupPager()
        }
        pager?.reload()
    }

    private fun setupPager() {
        pager = Pager(
            scope = viewModelScope,
            pager = object : AbstractPager<ReviewModel>() {
                override fun onError(t: Throwable) {
                    ReportHandler.reportError(t)
                    error = t.toUnknown()
                }
                override fun onLoadNext() { isLoadingNext = true }
                override fun onSuccess(result: List<ReviewModel?>) {
                    isLoading = false
                    isLoadingNext = false
                    isRefreshing = false
                    reviews = result.filterNotNull()
                }

                override fun onNoResults() {
                    isLoading = false
                }
            }, pagingFlow = {
                if (showOnlyPendingReviews) {
                    reviewsRepo.getPendingReviewsPaging(it)
                } else {
                    reviewsRepo.getReviewsPaging(it)
                }
            })
    }
}
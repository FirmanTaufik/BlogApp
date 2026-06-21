package com.time.yourguideapp.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.yourguideapp.data.repository.MainRepository
import com.time.yourguideapp.helper.AppManager
import com.time.yourguideapp.helper.AppLogger
import com.time.yourguideapp.presentation.home.HomeData
import com.time.yourguideapp.presentation.state.UIState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MainRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<UIState>(UIState.Loading)
    val state = _state.asStateFlow()
    private var observeJob: Job? = null
    private var homeDetailClickCount = 0

    init {
        loadContent()
    }

    fun refresh() {
        loadContent()
    }

    fun loadContent() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _state.value = UIState.Loading
            repository.observeHomeState(currentUserId()).collect { state ->
                val data  = (state  as? UIState.Success<*>)?.data
                val adMobConfig =( data as HomeData).adMobConfig
                homeDetailClickCount = adMobConfig.interstitialInterval
                _state.value = state
            }
        }
    }

    fun shouldShowHomeDetailInterstitial(interval: Int): Boolean {
        if (interval <= 0) {
            homeDetailClickCount = 0
            return false
        }

        homeDetailClickCount += 1
        if (homeDetailClickCount < interval) return false

        homeDetailClickCount = 0
        return true
    }

    fun toggleBookmark(postId: String) {
        if (postId.isBlank()) {
            AppLogger.e(tag = "Bookmark") { "Cannot toggle bookmark: postId is blank" }
            return
        }

        val userId = currentUserId() ?: return
        val currentData = (_state.value as? UIState.Success<*>)?.data as? HomeData ?: return
        val shouldRemove = currentData.bookmarkPostIds.contains(postId)
        val previousBookmarkPostIds = currentData.bookmarkPostIds

        updateBookmarkState(
            if (shouldRemove) {
                previousBookmarkPostIds.filterNot { it == postId }
            } else {
                (previousBookmarkPostIds + postId).distinct()
            }
        )

        viewModelScope.launch {
            runCatching {
                if (shouldRemove) {
                    repository.removeBookmark(userId, postId)
                } else {
                    repository.addBookmark(userId, postId)
                }
            }.onFailure { error ->
                updateBookmarkState(previousBookmarkPostIds)
                AppLogger.e(tag = "Bookmark", throwable = error) {
                    "Failed to ${if (shouldRemove) "remove" else "add"} bookmark for postId=$postId"
                }
            }
        }
    }

    private fun updateBookmarkState(bookmarkPostIds: List<String>) {
        val currentState = _state.value as? UIState.Success<*> ?: return
        val currentData = currentState.data as? HomeData ?: return
        _state.value = UIState.Success(
            currentData.copy(bookmarkPostIds = bookmarkPostIds)
        )
    }

    private fun currentUserId(): String? {
        return Firebase.auth.currentUser?.uid ?: AppManager.currentUserProfile?.uuid
    }
}

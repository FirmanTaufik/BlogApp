package com.time.yourguideapp.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.yourguideapp.data.repository.MainRepository
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
            repository.observeHomeState(Firebase.auth.currentUser?.uid).collect { state ->
                _state.value = state
            }
        }
    }

    fun toggleBookmark(postId: String) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val currentData = (_state.value as? UIState.Success<*>)?.data as? HomeData ?: return

        viewModelScope.launch {
            if (currentData.bookmarkPostIds.contains(postId)) {
                repository.removeBookmark(userId, postId)
            } else {
                repository.addBookmark(userId, postId)
            }
        }
    }
}

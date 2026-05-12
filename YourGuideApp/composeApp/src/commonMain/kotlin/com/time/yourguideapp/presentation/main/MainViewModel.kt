package com.time.yourguideapp.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.yourguideapp.data.repository.MainRepository
import com.time.yourguideapp.presentation.state.UIState
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
            repository.observeHomeState().collect { state ->
                _state.value = state
            }
        }
    }
}

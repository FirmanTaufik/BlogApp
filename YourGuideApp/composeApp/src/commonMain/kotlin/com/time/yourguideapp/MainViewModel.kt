package com.time.yourguideapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MainRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<UIState>(UIState.Loading)
    val state = _state.asStateFlow()

    init {
        loadContent()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = UIState.Loading
            delay(1500)
            _state.value = repository.getHomeState()
        }
    }

    fun loadContent() {
        viewModelScope.launch {
            _state.value = UIState.Loading
            _state.value = repository.getHomeState()
        }
    }
}

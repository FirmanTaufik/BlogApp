package com.time.yourguideapp.presentation.love

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.yourguideapp.data.repository.PopularPlacesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PopularPlacesViewModel(
    private val repository: PopularPlacesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PopularPlacesUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            runCatching {
                repository.loadPopularPlaces()
            }.fold(
                onSuccess = { places ->
                    _uiState.update {
                        it.copy(
                            places = places,
                            isLoading = false,
                            errorMessage = if (places.isEmpty()) {
                                "No popular places found"
                            } else {
                                null
                            },
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message,
                        )
                    }
                },
            )
        }
    }
}

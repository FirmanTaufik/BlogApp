package com.time.yourguideapp.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.yourguideapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun selectLocation(location: WeatherLocation) {
        if (location == _uiState.value.selectedLocation) return
        _uiState.update {
            it.copy(selectedLocation = location)
        }
        refresh()
    }

    fun refresh() {
        val location = _uiState.value.selectedLocation
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            runCatching {
                repository.loadWeatherForecast(location)
            }.fold(
                onSuccess = { forecast ->
                    _uiState.update {
                        it.copy(
                            forecast = forecast,
                            isLoading = false,
                            errorMessage = null,
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

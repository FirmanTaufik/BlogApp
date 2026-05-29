package com.time.yourguideapp.presentation.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.yourguideapp.data.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CurrencyViewModel(
    private val repository: CurrencyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CurrencyUiState(isLoading = true))
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
                repository.loadUsdExchangeRates()
            }.fold(
                onSuccess = { snapshot ->
                    _uiState.update {
                        it.copy(
                            rates = snapshot.rates,
                            isLoading = false,
                            errorMessage = null,
                            updatedDate = snapshot.date,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to load exchange rates.",
                        )
                    }
                },
            )
        }
    }
}

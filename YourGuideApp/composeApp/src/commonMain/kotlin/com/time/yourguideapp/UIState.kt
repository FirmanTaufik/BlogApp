package com.time.yourguideapp

sealed class UIState {
    object Loading : UIState()

    data class Success(
        val title: String,
        val message: String,
        val highlights: List<String>,
    ) : UIState()

    data class Error(val message: String) : UIState()
}

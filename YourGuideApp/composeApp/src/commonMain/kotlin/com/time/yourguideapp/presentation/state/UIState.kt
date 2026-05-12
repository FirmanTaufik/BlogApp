package com.time.yourguideapp.presentation.state

sealed class UIState {
    object Loading : UIState()

    data class Success<T>( var data : T ) : UIState()

    data class Error(val message: String) : UIState()
}

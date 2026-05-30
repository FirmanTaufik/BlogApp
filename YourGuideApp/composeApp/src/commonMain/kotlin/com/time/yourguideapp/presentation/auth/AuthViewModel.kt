package com.time.yourguideapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.time.yourguideapp.auth.GoogleSignInConfig
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authGateState = MutableStateFlow<AuthGateState>(AuthGateState.Loading)
    val authGateState: StateFlow<AuthGateState> = _authGateState.asStateFlow()

    private val _uiState = MutableStateFlow(
        AuthUiState(
            googleAuthProviderResult = createGoogleAuthProvider()
        )
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    init {
        observeAuthState()
    }

    fun onGoogleLoginStarted() {
        _uiState.value = _uiState.value.copy(
            isGoogleLoginLoading = true,
            loginError = null,
        )
    }

    fun onGoogleLoginResult(result: Result<FirebaseUser?>) {
        viewModelScope.launch {
            result
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(isGoogleLoginLoading = false)
                    if (user == null) {
                        _uiState.value = _uiState.value.copy(loginError = AuthLoginError.GoogleUserEmpty)
                    } else {
                        _uiState.value = _uiState.value.copy(loginError = null)
                        _events.emit(AuthEvent.LoginSuccess(user))
                    }
                }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isGoogleLoginLoading = false,
                        loginError = throwable.toAuthLoginError(),
                    )
                }
        }
    }

    fun onGoogleLoginConfigurationError() {
        val message = _uiState.value.googleAuthProviderResult.exceptionOrNull()?.message
        _uiState.value = _uiState.value.copy(
            loginError = AuthLoginError.Configuration(message),
        )
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            Firebase.auth.authStateChanged.collect { user ->
                _authGateState.value = if (user == null) {
                    AuthGateState.SignedOut
                } else {
                    AuthGateState.SignedIn(user)
                }
            }
        }
    }

    private fun createGoogleAuthProvider(): Result<GoogleAuthProvider> {
        return if (GoogleSignInConfig.isConfigured) {
            runCatching {
                GoogleAuthProvider.create(
                    credentials = GoogleAuthCredentials(serverId = GoogleSignInConfig.WEB_CLIENT_ID)
                )
            }
        } else {
            Result.failure(IllegalStateException())
        }
    }

    private fun Throwable.toAuthLoginError(): AuthLoginError {
        val rawMessage = message.orEmpty()
        return when {
            rawMessage.contains("idtoken is null", ignoreCase = true) ||
                rawMessage.contains("id token is null", ignoreCase = true) -> {
                AuthLoginError.IdTokenEmpty
            }

            rawMessage.isNotBlank() -> AuthLoginError.Raw(rawMessage)

            else -> AuthLoginError.Unknown(this::class.simpleName)
        }
    }
}

data class AuthUiState(
    val googleAuthProviderResult: Result<GoogleAuthProvider>,
    val isGoogleLoginLoading: Boolean = false,
    val loginError: AuthLoginError? = null,
)

sealed interface AuthGateState {
    data object Loading : AuthGateState
    data object SignedOut : AuthGateState
    data class SignedIn(val user: FirebaseUser) : AuthGateState
}

sealed interface AuthLoginError {
    data object GoogleUserEmpty : AuthLoginError
    data object IdTokenEmpty : AuthLoginError
    data class Raw(val message: String) : AuthLoginError
    data class Unknown(val className: String?) : AuthLoginError
    data class Configuration(val message: String?) : AuthLoginError
}

sealed interface AuthEvent {
    data class LoginSuccess(val user: FirebaseUser) : AuthEvent
}

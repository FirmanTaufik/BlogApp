package com.time.yourguideapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpauth.google.GoogleUser
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.time.yourguideapp.auth.GoogleSignInConfig
import com.time.yourguideapp.helper.AppManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider as FirebaseGoogleAuthProvider
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authGateState = MutableStateFlow(Firebase.auth.currentUser.toInitialAuthGateState())
    val authGateState: StateFlow<AuthGateState> = _authGateState.asStateFlow()
    private var signedOutConfirmationJob: Job? = null

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

    fun onGoogleLoginResult(user: GoogleUser?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGoogleLoginLoading = false)
            if (user == null) {
                _uiState.value = _uiState.value.copy(loginError = AuthLoginError.GoogleUserEmpty)
            } else {
                runCatching {
                    Firebase.auth.signInWithCredential(
                        FirebaseGoogleAuthProvider.credential(
                            idToken = user.idToken,
                            accessToken = user.accessToken,
                        )
                    )
                }.onSuccess { authResult ->
                    val firebaseUser = authResult.user
                    if (firebaseUser == null) {
                        _uiState.value = _uiState.value.copy(
                            loginError = AuthLoginError.Unknown("firebase user empty"),
                        )
                    } else {
                        saveUserProfile(firebaseUser, googleUser = user)
                        _uiState.value = _uiState.value.copy(loginError = null)
                        _events.emit(AuthEvent.LoginSuccess(firebaseUser))
                    }
                }.onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        loginError = throwable.toAuthLoginError(),
                    )
                }
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
                if (user == null) {
                    confirmSignedOutAfterRestoreWindow()
                } else {
                    signedOutConfirmationJob?.cancel()
                    saveUserProfile(user)
                    _authGateState.value = AuthGateState.SignedIn(user)
                }
            }
        }
    }

    private fun confirmSignedOutAfterRestoreWindow() {
        signedOutConfirmationJob?.cancel()
        signedOutConfirmationJob = viewModelScope.launch {
            _authGateState.value = AuthGateState.Loading
            delay(AUTH_RESTORE_WAIT_MS)
            val restoredUser = Firebase.auth.currentUser
            _authGateState.value = restoredUser.toResolvedAuthGateState()
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

private const val AUTH_RESTORE_WAIT_MS = 5_000L

private fun saveUserProfile(
    firebaseUser: FirebaseUser,
    googleUser: GoogleUser? = null,
) {
    AppManager.saveUserProfile(
        uuid = firebaseUser.uid,
        email = firebaseUser.email ?: googleUser?.email.orEmpty(),
        name = firebaseUser.displayName ?: googleUser?.displayName ?: firebaseUser.email ?: firebaseUser.uid,
        photoUrl = firebaseUser.photoURL ?: googleUser?.profilePicUrl.orEmpty(),
    )
}

private fun FirebaseUser?.toInitialAuthGateState(): AuthGateState {
    return if (this == null) {
        if (AppManager.currentUserProfile == null) {
            AuthGateState.Loading
        } else {
            AuthGateState.SignedIn()
        }
    } else {
        AuthGateState.SignedIn(this)
    }
}

private fun FirebaseUser?.toResolvedAuthGateState(): AuthGateState {
    return if (this == null) {
        if (AppManager.currentUserProfile == null) {
            AuthGateState.SignedOut
        } else {
            AuthGateState.SignedIn()
        }
    } else {
        AuthGateState.SignedIn(this)
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
    data class SignedIn(val user: FirebaseUser? = null) : AuthGateState
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

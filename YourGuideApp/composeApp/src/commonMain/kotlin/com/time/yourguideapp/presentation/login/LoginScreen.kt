package com.time.yourguideapp.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.time.yourguideapp.auth.GoogleSignInConfig
import dev.gitlive.firebase.auth.FirebaseUser
import org.jetbrains.compose.resources.stringResource
import yourguideapp.composeapp.generated.resources.*

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: (FirebaseUser) -> Unit = {},
) {
    var isLoading by remember { mutableStateOf(false) }
    var resultErrorMessage by remember { mutableStateOf<String?>(null) }
    var signedInUser by remember { mutableStateOf<FirebaseUser?>(null) }
    val googleWebClientMissingMessage = stringResource(Res.string.login_google_web_client_missing)
    val googleUserEmptyMessage = stringResource(Res.string.login_google_user_empty)
    val googleLoginFailedIdTokenMessage = stringResource(Res.string.login_google_login_failed_idtoken)
    val googleLoginFailedFallbackMessage = stringResource(Res.string.login_google_login_failed_fallback)
    val googleLoginFailedUnknownTemplate = stringResource(Res.string.login_google_login_failed_unknown)

    val googleAuthProviderResult = remember {
        if (GoogleSignInConfig.isConfigured) {
            runCatching {
                GoogleAuthProvider.create(
                    credentials = GoogleAuthCredentials(serverId = GoogleSignInConfig.WEB_CLIENT_ID)
                )
            }
        } else {
            Result.failure(IllegalStateException(googleWebClientMissingMessage))
        }
    }
    val configurationErrorMessage = googleAuthProviderResult.exceptionOrNull()?.message

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.login_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.login_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))

        if (googleAuthProviderResult.isSuccess) {
            GoogleButtonUiContainerFirebase(
                linkAccount = false,
                filterByAuthorizedAccounts = false,
                onResult = { result ->
                    isLoading = false
                    result
                        .onSuccess { user ->
                            if (user == null) {
                                resultErrorMessage = googleUserEmptyMessage
                            } else {
                                signedInUser = user
                                resultErrorMessage = null
                              //  onLoginSuccess(user)
                            }
                        }
                        .onFailure { throwable ->
                            resultErrorMessage = throwable.toGoogleLoginMessage(
                                idTokenMessage = googleLoginFailedIdTokenMessage,
                                fallbackMessage = googleLoginFailedFallbackMessage,
                                unknownTemplate = googleLoginFailedUnknownTemplate,
                            )
                        }
                }
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    onClick = {
                        isLoading = true
                        resultErrorMessage = null
                        this.onClick()
                    }
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(stringResource(Res.string.login_google_button))
                    }
                }
            }
        } else {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                onClick = {},
            ) {
                Text(stringResource(Res.string.login_google_button))
            }
        }

        val user = signedInUser
        if (user != null) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(
                    Res.string.login_signed_in_as,
                    user.displayName ?: user.email ?: user.uid,
                ),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }

        val error = configurationErrorMessage ?: resultErrorMessage
        if (error != null) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun Throwable.toGoogleLoginMessage(
    idTokenMessage: String,
    fallbackMessage: String,
    unknownTemplate: String,
): String {
    val rawMessage = message.orEmpty()
    return when {
        rawMessage.contains("idtoken is null", ignoreCase = true) ||
            rawMessage.contains("id token is null", ignoreCase = true) -> {
            idTokenMessage
        }
        rawMessage.isNotBlank() -> rawMessage
        else -> unknownTemplate.replace("%1\$s", this::class.simpleName ?: fallbackMessage)
    }
}

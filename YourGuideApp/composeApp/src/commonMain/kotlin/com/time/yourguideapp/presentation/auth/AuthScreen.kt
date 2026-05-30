package com.time.yourguideapp.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.uihelper.apple.AppleSignInButton
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.AppColors.blue4789d7
import com.time.yourguideapp.auth.GoogleSignInConfig
import com.time.yourguideapp.core.platform.getAppName
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.helper.rootBackground
import com.time.yourguideapp.presentation.component.ButtonView
import com.time.yourguideapp.presentation.component.HorizontalSpacer
import com.time.yourguideapp.presentation.component.InputView
import com.time.yourguideapp.presentation.component.TabView
import com.time.yourguideapp.presentation.component.VerticalSpacer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import org.jetbrains.compose.resources.stringResource
import yourguideapp.composeapp.generated.resources.*


@Composable
fun AuthScreen (
    modifier: Modifier = Modifier,
    onLoginSuccess: (FirebaseUser) -> Unit = {},
   onSkipLogin : () -> Unit
) {

    val currentUser by Firebase.auth.authStateChanged.collectAsState(Firebase.auth.currentUser)
    LaunchedEffect(currentUser){
        if (currentUser!=null){
            onSkipLogin()
        }
    }

    var isLoading by remember { mutableStateOf(false) }
    var resultErrorMessage by remember { mutableStateOf<String?>(null) }
    val googleWebClientMissingMessage = stringResource(Res.string.auth_google_web_client_missing)
    val googleUserEmptyMessage = stringResource(Res.string.auth_google_user_empty)
    val googleLoginFailedIdTokenMessage = stringResource(Res.string.auth_google_login_failed_idtoken)
    val googleLoginFailedFallbackMessage = stringResource(Res.string.auth_google_login_failed_fallback)
    val googleLoginFailedUnknownTemplate = stringResource(Res.string.auth_google_login_failed_unknown)

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

    Scaffold(
    modifier = modifier
    .fillMaxSize()
    .rootBackground(),
    containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            VerticalSpacer(20)
            ContentHeader(onSkipLogin)
            VerticalSpacer(20)
            ContentAuth(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                googleAuthProviderResult = googleAuthProviderResult,
                googleConfigurationErrorMessage = configurationErrorMessage,
                isGoogleLoginLoading = isLoading,
                onGoogleLoginStarted = {
                    isLoading = true
                    resultErrorMessage = null
                },
                onGoogleLoginResult = { result ->
                    isLoading = false
                    result
                        .onSuccess { user ->
                            if (user == null) {
                                resultErrorMessage = googleUserEmptyMessage
                            } else {
                                resultErrorMessage = null
                                onLoginSuccess(user)
                            }
                        }
                        .onFailure { throwable ->
                            resultErrorMessage = throwable.toGoogleLoginMessage(
                                idTokenMessage = googleLoginFailedIdTokenMessage,
                                fallbackMessage = googleLoginFailedFallbackMessage,
                                unknownTemplate = googleLoginFailedUnknownTemplate,
                            )
                        }
                },
                onGoogleLoginConfigurationError = {
                    resultErrorMessage = configurationErrorMessage
                }
            )
        }
    }
}

@Composable
private fun ContentHeader(onSkipLogin: () -> Unit) {
    val appName = getAppName()
    val title = stringResource(Res.string.auth_enter_your_space)
    val description = stringResource(Res.string.auth_description)

    Column(modifier = Modifier.padding(top = 20.dp, end = 10.dp, start = 10.dp)) {
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                appName, fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = AppColors.white,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            ButtonView(text = stringResource(Res.string.common_skip), roundShape = 50) {
                onSkipLogin()
            }

        }
        VerticalSpacer(10)
        Text(
            title, fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            color = AppColors.white,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            description,
            color = AppColors.white,
        )
    }

}

@Composable
private fun ContentAuth(
    modifier: Modifier = Modifier,
    googleAuthProviderResult: Result<GoogleAuthProvider>,
    googleConfigurationErrorMessage: String?,
    isGoogleLoginLoading: Boolean,
    onGoogleLoginStarted: () -> Unit,
    onGoogleLoginResult: (Result<FirebaseUser?>) -> Unit,
    onGoogleLoginConfigurationError: () -> Unit,
) {
    val tabsList = listOf(
        stringResource(Res.string.auth_login_tab),
        stringResource(Res.string.auth_signup_tab),
    )
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(1) }
    Card(
        shape = RoundedCornerShape(topEnd = 40.dp, topStart = 40.dp),
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.cardColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            VerticalSpacer(30)
            TabView(tabsList, selectedTabIndex){
                selectedTabIndex = it
            }

            when (selectedTabIndex) {
                0 ->{
                    LoginContent(Modifier)
                }
                else ->{
                    RegisterContent(Modifier)
                }
            }

            VerticalSpacer(20)
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp)) {
                AppleSignInButton (text = stringResource(Res.string.auth_apple_button),
                    modifier = Modifier.weight(1f) ){

                }
                HorizontalSpacer(10)
                if (googleAuthProviderResult.isSuccess) {
                    GoogleButtonUiContainerFirebase(
                        linkAccount = false,
                        filterByAuthorizedAccounts = false,
                        onResult = onGoogleLoginResult
                    ) {
                        GoogleSignInButton (text = stringResource(Res.string.auth_google_button),
                            modifier = Modifier
                                .glassmorphism()
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(50)) {
                            if (!isGoogleLoginLoading) {
                                onGoogleLoginStarted()
                                this.onClick()
                            }
                        }
                    }
                } else {
                    GoogleSignInButton (text = stringResource(Res.string.auth_google_button),
                        modifier = Modifier
                            .glassmorphism()
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(50)) {
                        if (googleConfigurationErrorMessage != null) {
                            onGoogleLoginConfigurationError()
                        }
                    }
                }
            }

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

@Composable
fun RegisterContent(modifier : Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Column {
    Column(modifier = Modifier.fillMaxWidth()
            .padding(20.dp)) {
            Text(stringResource(Res.string.auth_name), color = Color.Black.copy(alpha = 0.5f))
            VerticalSpacer(10)
            InputView(leadingIcon = Icons.Default.AccountCircle, placeHolder = stringResource(Res.string.auth_enter_name),
                input = name) {
                name = it
            }
            VerticalSpacer(10)
            Text(stringResource(Res.string.auth_email), color = Color.Black.copy(alpha = 0.5f))
            VerticalSpacer(10)
            InputView(leadingIcon = Icons.Default.Email, placeHolder = stringResource(Res.string.auth_enter_email),
                input = email) {
                email = it
            }
            VerticalSpacer(30)
            Text(stringResource(Res.string.auth_password), color = Color.Black.copy(alpha = 0.5f))
            VerticalSpacer(10)
            InputView(leadingIcon = Icons.Default.Password, placeHolder = stringResource(Res.string.auth_enter_password), isInputTypePassword = true,
                input = password) {
                password = it
            }


            VerticalSpacer(25)

            Button(onClick = {

            }, colors = ButtonDefaults.buttonColors(containerColor = blue4789d7),
                modifier = Modifier
                    .fillMaxWidth().height(50.dp)){
                Text(stringResource(Res.string.auth_sign_up))
            }
            VerticalSpacer(25)

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(stringResource(Res.string.auth_or_sign_up_with),
                    modifier = Modifier.padding(horizontal = 10.dp),
                    color = Color.Gray.copy(alpha = 0.5f))
                HorizontalDivider(modifier = Modifier.weight(1f))

            }

        }
    }
}

@Composable
private fun LoginContent(modifier: Modifier){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(modifier = modifier.fillMaxWidth()
        .padding(20.dp)) {
        Text(stringResource(Res.string.auth_email), color = Color.Black.copy(alpha = 0.5f))
        VerticalSpacer(10)
        InputView(leadingIcon = Icons.Default.Email, placeHolder = stringResource(Res.string.auth_enter_email),
            input = email) {
            email = it
        }
        VerticalSpacer(30)
        Text(stringResource(Res.string.auth_password), color = Color.Black.copy(alpha = 0.5f))
        VerticalSpacer(10)
        InputView(leadingIcon = Icons.Default.Password, placeHolder = stringResource(Res.string.auth_enter_password), isInputTypePassword = true,
            input = password) {
            password = it
        }

        VerticalSpacer(10)
        Row (modifier = Modifier.fillMaxWidth()){
            Spacer(Modifier.weight(1f))
            Text(stringResource(Res.string.auth_forgot_password), color = Color.Black.copy(alpha = 0.5f))

        }
        VerticalSpacer(25)

        Button(onClick = {

        }, colors = ButtonDefaults.buttonColors(containerColor = blue4789d7),
            modifier = Modifier
                .fillMaxWidth().height(50.dp)){
            Text(stringResource(Res.string.auth_log_in))
        }
        VerticalSpacer(25)

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(stringResource(Res.string.auth_or_log_in_with),
                modifier = Modifier.padding(horizontal = 10.dp),
                color = Color.Gray.copy(alpha = 0.5f))
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

    }
}


@Composable
@Preview(showSystemUi = true, showBackground = true)
fun AuthScreenPreview() {
    MaterialTheme {
        AuthScreen(){

        }
    }
}

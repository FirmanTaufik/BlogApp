package com.time.yourguideapp.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.helper.rootBackground
import com.time.yourguideapp.presentation.auth.AuthGateState
import com.time.yourguideapp.presentation.auth.AuthRoute
import com.time.yourguideapp.presentation.auth.AuthViewModel
import com.time.yourguideapp.presentation.main.MainScreen
import org.koin.compose.viewmodel.koinViewModel

data object RootScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authViewModel = koinViewModel<AuthViewModel>()
        val authGateState by authViewModel.authGateState.collectAsState()

        LaunchedEffect(authGateState) {
            if (authGateState is AuthGateState.SignedIn && navigator.lastItem !is MainScreen) {
                navigator.push(MainScreen)
            }
        }

        when (authGateState) {
            AuthGateState.Loading,
            is AuthGateState.SignedIn -> AuthGateLoading()

            AuthGateState.SignedOut -> {
                AuthRoute(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        if (navigator.lastItem !is MainScreen) {
                            navigator.push(MainScreen)
                        }
                    },
                    onSkipLogin = {
                        if (navigator.lastItem !is MainScreen) {
                            navigator.push(MainScreen)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun AuthGateLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .rootBackground(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = AppColors.blue123060)
    }
}

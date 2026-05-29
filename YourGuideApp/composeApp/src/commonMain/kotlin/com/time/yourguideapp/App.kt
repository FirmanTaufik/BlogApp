package com.time.yourguideapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.mmk.kmpauth.core.KMPAuthInternalApi
import com.mmk.kmpauth.core.logger.KMPAuthLogger
import com.mmk.kmpauth.core.logger.currentLogger
import com.time.yourguideapp.di.appModule
import com.time.yourguideapp.di.platformModule
import com.time.yourguideapp.helper.ProvideAppLanguage
import com.time.yourguideapp.presentation.auth.AuthScreen
import com.time.yourguideapp.presentation.login.LoginScreen
import com.time.yourguideapp.presentation.main.MainViewModel
import com.time.yourguideapp.root.RootScreen
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.KoinApplication
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinConfiguration

val LocalMainViewModel = staticCompositionLocalOf<MainViewModel> {
    error("MainViewModel is not provided")
}

val LocalRootNavigator = staticCompositionLocalOf<Navigator> {
    error("Root Navigator is not provided")
}

@Composable
@Preview
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {
    remember { installKmpAuthLogger() }

    KoinApplication(configuration = koinConfiguration(declaration = {
        koinAppDeclaration?.invoke(this)
        modules(appModule, platformModule)
    }), content = {
        ProvideAppLanguage {
            MaterialTheme {
                val currentUser by Firebase.auth.authStateChanged.collectAsState(Firebase.auth.currentUser)

                if (currentUser == null) {
                    AuthScreen(modifier = Modifier)
                } else {
                    val viewModel = koinViewModel<MainViewModel>()

                    CompositionLocalProvider(LocalMainViewModel provides viewModel) {
                        Navigator(screen = RootScreen) { navigator ->
                            CompositionLocalProvider(LocalRootNavigator provides navigator) {
                                CurrentScreen()
                            }
                        }
                    }
                }
            }
        }
    })
}

@OptIn(KMPAuthInternalApi::class)
private fun installKmpAuthLogger() {
    currentLogger = KMPAuthLogger { message ->
        println("KMPAuth: $message")
    }
}

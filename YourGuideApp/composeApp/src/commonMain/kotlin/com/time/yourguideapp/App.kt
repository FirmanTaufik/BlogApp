package com.time.yourguideapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.time.yourguideapp.di.appModule
import com.time.yourguideapp.di.platformModule
import com.time.yourguideapp.presentation.main.MainViewModel
import com.time.yourguideapp.root.RootScreen
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.KoinAppDeclaration

val LocalMainViewModel = staticCompositionLocalOf<MainViewModel> {
    error("MainViewModel is not provided")
}

val LocalRootNavigator = staticCompositionLocalOf<Navigator> {
    error("Root Navigator is not provided")
}

@Composable
@Preview
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {
    KoinApplication(
        application = {
            koinAppDeclaration?.invoke(this)
            modules(appModule, platformModule)
        }
    ) {
        MaterialTheme {
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

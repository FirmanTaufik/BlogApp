package com.time.yourguideapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.mmk.kmpauth.core.KMPAuthInternalApi
import com.mmk.kmpauth.core.logger.KMPAuthLogger
import com.mmk.kmpauth.core.logger.currentLogger
import com.time.yourguideapp.data.repository.PopularPlacesRepository
import com.time.yourguideapp.di.appModule
import com.time.yourguideapp.di.platformModule
import com.time.yourguideapp.helper.ProvideAppLanguage
import com.time.yourguideapp.presentation.auth.AuthScreen
import com.time.yourguideapp.presentation.ads.AppOpenAdEffect
import com.time.yourguideapp.presentation.home.HomeData
import com.time.yourguideapp.presentation.home.HomeScreen
import com.time.yourguideapp.presentation.login.LoginScreen
import com.time.yourguideapp.presentation.main.MainViewModel
import com.time.yourguideapp.presentation.state.UIState
import com.time.yourguideapp.presentation.splash.SplashScreen
import com.time.yourguideapp.root.RootScreen
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
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
                var showSplash by remember { mutableStateOf(true) }
                val popularPlacesRepository = koinInject<PopularPlacesRepository>()

                LaunchedEffect(Unit) {
                    coroutineScope {
                        val minimumSplashDuration = async { delay(2200) }
                        val popularPlacesPreload = async {
                            popularPlacesRepository.loadPopularPlaces()
                        }

                        minimumSplashDuration.await()
                        popularPlacesPreload.await()
                    }
                    showSplash = false
                }

                if (showSplash) {
                    SplashScreen()
                }  else {
                    val viewModel = koinViewModel<MainViewModel>()
                    val mainState by viewModel.state.collectAsState()
                    val adMobConfig = ((mainState as? UIState.Success<*>)?.data as? HomeData)
                        ?.adMobConfig

                    AppOpenAdEffect(
                        adUnitId = adMobConfig?.appOpenAdUnitId.orEmpty(),
                        enabled = adMobConfig?.canShowAppOpen == true,
                    )

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

package com.time.yourguideapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.time.yourguideapp.di.appModule
import com.time.yourguideapp.di.platformModule
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.KoinAppDeclaration

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
            val state by viewModel.state.collectAsState()

            HomeScreen(
                state = state,
                onReload = viewModel::refresh,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .safeContentPadding()
                    .fillMaxSize(),
            )
        }
    }
}

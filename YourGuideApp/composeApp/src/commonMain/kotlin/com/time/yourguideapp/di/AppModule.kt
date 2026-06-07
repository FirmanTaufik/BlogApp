package com.time.yourguideapp.di

import com.time.yourguideapp.data.remote.FirestoreGuideService
import com.time.yourguideapp.data.repository.CurrencyRepository
import com.time.yourguideapp.data.repository.MainRepository
import com.time.yourguideapp.data.repository.PopularPlacesRepository
import com.time.yourguideapp.data.repository.WeatherRepository
import com.time.yourguideapp.helper.AppLogger
import com.time.yourguideapp.presentation.auth.AuthViewModel
import com.time.yourguideapp.presentation.currency.CurrencyViewModel
import com.time.yourguideapp.presentation.category.CategoryViewModel
import com.time.yourguideapp.presentation.main.MainViewModel
import com.time.yourguideapp.presentation.love.PopularPlacesViewModel
import com.time.yourguideapp.presentation.weather.WeatherViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient {
            if (AppLogger.isDebugEnabled) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            AppLogger.d(tag = "HTTP") { message }
                        }
                    }
                    level = LogLevel.ALL
                    sanitizeHeader { header ->
                        header == HttpHeaders.Authorization ||
                            header == HttpHeaders.Cookie ||
                            header == HttpHeaders.SetCookie
                    }
                }
            }
        }
    }
    single { FirestoreGuideService() }
    single { MainRepository(get(), get()) }
    single { WeatherRepository(get()) }
    single { CurrencyRepository(get()) }
    single { PopularPlacesRepository(get()) }
    viewModelOf(::MainViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::CategoryViewModel)
    viewModelOf(::WeatherViewModel)
    viewModelOf(::CurrencyViewModel)
    viewModelOf(::PopularPlacesViewModel)
}

expect val platformModule: Module

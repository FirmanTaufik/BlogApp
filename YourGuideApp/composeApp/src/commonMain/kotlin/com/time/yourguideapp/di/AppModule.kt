package com.time.yourguideapp.di

import com.time.yourguideapp.data.remote.FirestoreGuideService
import com.time.yourguideapp.data.repository.MainRepository
import com.time.yourguideapp.data.repository.WeatherRepository
import com.time.yourguideapp.presentation.category.CategoryViewModel
import com.time.yourguideapp.presentation.main.MainViewModel
import com.time.yourguideapp.presentation.weather.WeatherViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { HttpClient() }
    single { FirestoreGuideService() }
    single { MainRepository(get(), get()) }
    single { WeatherRepository(get()) }
    viewModelOf(::MainViewModel)
    viewModelOf(::CategoryViewModel)
    viewModelOf(::WeatherViewModel)
}

expect val platformModule: Module

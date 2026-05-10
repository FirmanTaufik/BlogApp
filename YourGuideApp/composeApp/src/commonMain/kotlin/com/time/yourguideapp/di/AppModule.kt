package com.time.yourguideapp.di

import com.time.yourguideapp.FirestoreGuideService
import com.time.yourguideapp.MainRepository
import com.time.yourguideapp.MainViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { FirestoreGuideService() }
    single { MainRepository(get(), get()) }
    viewModelOf(::MainViewModel)
}

expect val platformModule: Module

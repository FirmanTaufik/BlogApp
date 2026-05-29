package com.time.yourguideapp.di

import com.time.yourguideapp.data.remote.FirestoreGuideService
import com.time.yourguideapp.data.repository.MainRepository
import com.time.yourguideapp.presentation.category.CategoryViewModel
import com.time.yourguideapp.presentation.main.MainViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { FirestoreGuideService() }
    single { MainRepository(get(), get()) }
    viewModelOf(::MainViewModel)
    viewModelOf(::CategoryViewModel)
}

expect val platformModule: Module

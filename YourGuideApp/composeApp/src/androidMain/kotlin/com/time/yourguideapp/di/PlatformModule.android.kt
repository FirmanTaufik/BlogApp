package com.time.yourguideapp.di

import com.time.yourguideapp.core.platform.Platform
import com.time.yourguideapp.core.platform.getPlatform
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<Platform> { getPlatform() }
}

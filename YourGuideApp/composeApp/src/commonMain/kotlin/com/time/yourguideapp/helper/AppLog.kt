package com.time.yourguideapp.helper

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

object AppLogger {
    var isDebugEnabled: Boolean = false
        private set

    // Fungsi untuk inisialisasi awal (dipanggil dari iOS & Android)
    fun setUp(isDebug: Boolean) {
        isDebugEnabled = isDebug
        if (isDebug) {
            Napier.base(DebugAntilog())
        }
        // Jika false (Release), Napier tidak diberi Ant sehingga log tidak akan muncul/dieksekusi
    }

    fun d(tag: String? = null, message: () -> String) {
        Napier.d(tag = tag, message = message)
    }

    fun e(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        Napier.e(tag = tag, throwable = throwable, message = message)
    }

    fun i(tag: String? = null, message: () -> String) {
        Napier.i(tag = tag, message = message)
    }
}

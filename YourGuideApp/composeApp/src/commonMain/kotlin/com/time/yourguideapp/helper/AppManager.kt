package com.time.yourguideapp.helper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppManager {
    var currentLanguage by mutableStateOf("id")
}

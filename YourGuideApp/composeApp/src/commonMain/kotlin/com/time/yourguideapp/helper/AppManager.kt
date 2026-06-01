package com.time.yourguideapp.helper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppManager {
    private const val DEFAULT_LANGUAGE = "en"

    private var initialized = false
    private var userProfileInitialized = false
    private var _currentLanguage by mutableStateOf(DEFAULT_LANGUAGE)
    private var _currentUserProfile by mutableStateOf<UserProfile?>(null)

    var currentLanguage: String
        get() {
            ensureInitialized()
            return _currentLanguage
        }
        set(value) {
            ensureInitialized()
            _currentLanguage = value
            LanguageStorage.saveLanguage(value)
        }

    fun initializeLanguage() {
        if (initialized) return
        _currentLanguage = LanguageStorage.loadLanguage() ?: DEFAULT_LANGUAGE
        initialized = true
    }

    val currentUserProfile: UserProfile?
        get() {
            ensureUserProfileInitialized()
            return _currentUserProfile
        }

    fun initializeUserProfile() {
        if (userProfileInitialized) return
        _currentUserProfile = UserProfileStorage.loadUserProfile()
        userProfileInitialized = true
    }

    fun saveUserProfile(
        uuid: String,
        email: String,
        name: String,
        photoUrl: String,
    ) {
        ensureUserProfileInitialized()
        val profile = UserProfile(
            uuid = uuid,
            email = email,
            name = name,
            photoUrl = photoUrl,
        )
        _currentUserProfile = profile
        UserProfileStorage.saveUserProfile(profile)
    }

    fun clearUserProfile() {
        ensureUserProfileInitialized()
        _currentUserProfile = null
        UserProfileStorage.clearUserProfile()
    }

    private fun ensureInitialized() {
        if (!initialized) {
            initializeLanguage()
        }
    }

    private fun ensureUserProfileInitialized() {
        if (!userProfileInitialized) {
            initializeUserProfile()
        }
    }
}

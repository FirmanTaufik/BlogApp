package com.time.yourguideapp.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual object UserProfileStorage {
    private var dataStore: DataStore<Preferences>? = null

    actual fun initialize(context: Any?) {
        // No-op on iOS. DataStore is created lazily on first access.
    }

    actual fun loadUserProfile(): UserProfile? = runBlocking {
        val preferences = requireDataStore().data.first()
        val uuid = preferences[USER_UUID_KEY].orEmpty()
        if (uuid.isBlank()) {
            null
        } else {
            UserProfile(
                uuid = uuid,
                email = preferences[USER_EMAIL_KEY].orEmpty(),
                name = preferences[USER_NAME_KEY].orEmpty(),
                photoUrl = preferences[USER_PHOTO_URL_KEY].orEmpty(),
            )
        }
    }

    actual fun saveUserProfile(profile: UserProfile) {
        runBlocking {
            requireDataStore().edit { preferences ->
                preferences[USER_UUID_KEY] = profile.uuid
                preferences[USER_EMAIL_KEY] = profile.email
                preferences[USER_NAME_KEY] = profile.name
                preferences[USER_PHOTO_URL_KEY] = profile.photoUrl
            }
        }
    }

    actual fun clearUserProfile() {
        runBlocking {
            requireDataStore().edit { preferences ->
                preferences.remove(USER_UUID_KEY)
                preferences.remove(USER_EMAIL_KEY)
                preferences.remove(USER_NAME_KEY)
                preferences.remove(USER_PHOTO_URL_KEY)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun requireDataStore(): DataStore<Preferences> {
        return dataStore ?: createUserProfileDataStore {
            val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = true,
                error = null,
            )
            val directoryPath = requireNotNull(documentDirectory?.path)
            "$directoryPath/$USER_PROFILE_DATA_STORE_FILE_NAME"
        }.also { dataStore = it }
    }
}

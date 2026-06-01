package com.time.yourguideapp.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

actual object UserProfileStorage {
    private var dataStore: DataStore<Preferences>? = null
    private var appContext: Context? = null

    actual fun initialize(context: Any?) {
        if (dataStore != null) return

        val appContext = context as? Context
            ?: error("UserProfileStorage.initialize(context) requires an Android Context")

        this.appContext = appContext.applicationContext
        dataStore = createUserProfileDataStore {
            this.appContext!!.filesDir.resolve(USER_PROFILE_DATA_STORE_FILE_NAME).absolutePath
        }
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

    private fun requireDataStore(): DataStore<Preferences> {
        return dataStore ?: error("UserProfileStorage.initialize(context) must be called before use")
    }
}

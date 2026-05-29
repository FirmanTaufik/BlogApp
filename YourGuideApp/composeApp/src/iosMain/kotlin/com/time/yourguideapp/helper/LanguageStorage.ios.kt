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
import platform.Foundation.NSUserDefaults

actual object LanguageStorage {
    private const val LEGACY_KEY_LANGUAGE = "current_language"

    private var dataStore: DataStore<Preferences>? = null
    private var migratedFromLegacy = false

    actual fun initialize(context: Any?) {
        // No-op on iOS. DataStore is created lazily on first access.
    }

    actual fun loadLanguage(): String? = runBlocking {
        val store = requireDataStore()
        migrateLegacyLanguageIfNeeded(store)
        store.data.first()[LANGUAGE_KEY]
    }

    actual fun saveLanguage(language: String) {
        runBlocking {
            val store = requireDataStore()
            migrateLegacyLanguageIfNeeded(store)
            store.edit { preferences ->
                preferences[LANGUAGE_KEY] = language
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun requireDataStore(): DataStore<Preferences> {
        return dataStore ?: createLanguageDataStore {
            val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = true,
                error = null,
            )
            val directoryPath = requireNotNull(documentDirectory?.path)
            "$directoryPath/$LANGUAGE_DATA_STORE_FILE_NAME"
        }.also { dataStore = it }
    }

    private suspend fun migrateLegacyLanguageIfNeeded(store: DataStore<Preferences>) {
        if (migratedFromLegacy) return

        val currentLanguage = store.data.first()[LANGUAGE_KEY]
        if (currentLanguage == null) {
            val legacyLanguage = NSUserDefaults.standardUserDefaults.stringForKey(LEGACY_KEY_LANGUAGE)
            if (legacyLanguage != null) {
                store.edit { preferences ->
                    preferences[LANGUAGE_KEY] = legacyLanguage
                }
            }
        }

        migratedFromLegacy = true
    }
}

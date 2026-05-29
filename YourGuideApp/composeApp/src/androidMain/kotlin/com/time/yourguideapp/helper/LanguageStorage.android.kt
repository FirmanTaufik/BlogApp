package com.time.yourguideapp.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

actual object LanguageStorage {
    private const val LEGACY_PREF_NAME = "yourguide_prefs"
    private const val LEGACY_KEY_LANGUAGE = "current_language"

    private var dataStore: DataStore<Preferences>? = null
    private var appContext: Context? = null
    private var migratedFromLegacy = false

    actual fun initialize(context: Any?) {
        if (dataStore != null) return

        val appContext = context as? Context
            ?: error("LanguageStorage.initialize(context) requires an Android Context")

        this.appContext = appContext.applicationContext
        dataStore = createLanguageDataStore {
            this.appContext!!.filesDir.resolve(LANGUAGE_DATA_STORE_FILE_NAME).absolutePath
        }
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

    private fun requireDataStore(): DataStore<Preferences> {
        return dataStore ?: error("LanguageStorage.initialize(context) must be called before use")
    }

    private suspend fun migrateLegacyLanguageIfNeeded(store: DataStore<Preferences>) {
        if (migratedFromLegacy) return

        val currentLanguage = store.data.first()[LANGUAGE_KEY]
        if (currentLanguage == null) {
            val legacyLanguage = readLegacyLanguage()
            if (legacyLanguage != null) {
                store.edit { preferences ->
                    preferences[LANGUAGE_KEY] = legacyLanguage
                }
            }
        }

        migratedFromLegacy = true
    }

    private fun readLegacyLanguage(): String? {
        val context = appContext ?: return null
        val legacyPrefs = context.getSharedPreferences(LEGACY_PREF_NAME, Context.MODE_PRIVATE)
        return legacyPrefs.getString(LEGACY_KEY_LANGUAGE, null)
    }
}

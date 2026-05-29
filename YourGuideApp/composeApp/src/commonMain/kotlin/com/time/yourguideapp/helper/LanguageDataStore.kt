package com.time.yourguideapp.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

internal const val LANGUAGE_DATA_STORE_FILE_NAME = "yourguide_language.preferences_pb"

internal fun createLanguageDataStore(producePath: () -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() },
    )
}

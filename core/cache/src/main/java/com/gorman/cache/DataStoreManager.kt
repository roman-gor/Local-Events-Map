package com.gorman.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_CURRENT_UID = stringPreferencesKey("current_uid")
    }

    val currentUid: Flow<String?> = dataStore.data.map { prefs -> prefs[KEY_CURRENT_UID] }

    suspend fun saveCurrentUid(uid: String) {
        dataStore.edit { prefs ->
            prefs[KEY_CURRENT_UID] = uid
        }
    }
}

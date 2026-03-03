package com.gorman.cache.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_LAST_SYNC = longPreferencesKey("key_last_sync")
    }

    val lastSyncTimestamp: Flow<Long?> = dataStore.data.map { prefs -> prefs[KEY_LAST_SYNC] }

    suspend fun saveSyncTimestamp(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[KEY_LAST_SYNC] = timestamp
        }
    }
}

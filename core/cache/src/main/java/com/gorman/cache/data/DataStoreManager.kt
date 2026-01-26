package com.gorman.cache.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gorman.common.models.CityData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class DataStoreManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_CITY_DATA = stringPreferencesKey("city_data")
        private val USER_ID = stringPreferencesKey("user_id")
        private val KEY_LAST_SYNC = longPreferencesKey("key_last_sync")
    }

    val saveUserId: Flow<String?> = context.dataStore.data.map { prefs -> prefs[USER_ID] }
    val lastSyncTimestamp: Flow<Long?> = context.dataStore.data.map { prefs -> prefs[KEY_LAST_SYNC] }

    val savedCity: Flow<CityData?> = context.dataStore.data.map { prefs ->
        val jsonString = prefs[KEY_CITY_DATA]
        if (jsonString != null) {
            Json.decodeFromString<CityData>(jsonString)
        } else {
            null
        }
    }

    suspend fun saveSyncTimestamp(timestamp: Long) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_SYNC] = timestamp
        }
    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = userId
        }
    }

    suspend fun saveCity(cityData: CityData) {
        context.dataStore.edit { prefs ->
            prefs[KEY_CITY_DATA] = Json.encodeToString(cityData)
        }
    }
}

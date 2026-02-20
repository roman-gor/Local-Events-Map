package com.gorman.cache.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gorman.common.models.CityData
import com.gorman.common.models.FiltersState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_CITY_DATA = stringPreferencesKey("city_data")
        private val KEY_LAST_SYNC = longPreferencesKey("key_last_sync")
        private val KEY_FILTERS_STATE = stringPreferencesKey("key_filters_state")
    }

    val lastSyncTimestamp: Flow<Long?> = dataStore.data.map { prefs -> prefs[KEY_LAST_SYNC] }
    val savedCity: Flow<CityData?> = dataStore.data.map { prefs ->
        val jsonString = prefs[KEY_CITY_DATA]
        if (jsonString != null) {
            Json.decodeFromString<CityData>(jsonString)
        } else {
            null
        }
    }
    val savedFilters: Flow<FiltersState?> = dataStore.data.map { prefs ->
        val jsonString = prefs[KEY_FILTERS_STATE]
        if (jsonString != null) {
            Json.decodeFromString<FiltersState>(jsonString)
        } else {
            null
        }
    }

    suspend fun saveSyncTimestamp(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[KEY_LAST_SYNC] = timestamp
        }
    }

    suspend fun saveCity(cityData: CityData) {
        dataStore.edit { prefs ->
            prefs[KEY_CITY_DATA] = Json.encodeToString(cityData)
        }
    }

    suspend fun saveFiltersState(state: FiltersState) {
        dataStore.edit { prefs ->
            prefs[KEY_FILTERS_STATE] = Json.encodeToString(state)
        }
    }
}

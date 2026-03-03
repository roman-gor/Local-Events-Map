package com.gorman.cache.data

import com.gorman.data.cache.IPreferencesDataSource
import javax.inject.Inject

internal class PreferencesDataSource @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : IPreferencesDataSource {
    override val lastSyncTimestamp = dataStoreManager.lastSyncTimestamp

    override suspend fun saveSyncTimestamp(timestamp: Long) {
        dataStoreManager.saveSyncTimestamp(timestamp)
    }
}

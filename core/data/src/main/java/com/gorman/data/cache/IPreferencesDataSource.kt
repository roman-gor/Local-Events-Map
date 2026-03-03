package com.gorman.data.cache

import kotlinx.coroutines.flow.Flow

interface IPreferencesDataSource {
    val lastSyncTimestamp: Flow<Long?>
    suspend fun saveSyncTimestamp(timestamp: Long)
}

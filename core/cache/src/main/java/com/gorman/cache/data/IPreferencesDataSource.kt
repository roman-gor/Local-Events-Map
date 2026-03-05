package com.gorman.cache.data

import kotlinx.coroutines.flow.Flow

interface IPreferencesDataSource {
    val currentUid: Flow<String?>
    suspend fun saveCurrentUid(uid: String)
}

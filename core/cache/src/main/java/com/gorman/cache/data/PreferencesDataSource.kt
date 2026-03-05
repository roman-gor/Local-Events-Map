package com.gorman.cache.data

import com.gorman.cache.DataStoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class PreferencesDataSource @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : IPreferencesDataSource {
    override val currentUid: Flow<String?> = dataStoreManager.currentUid

    override suspend fun saveCurrentUid(uid: String) { dataStoreManager.saveCurrentUid(uid) }
}

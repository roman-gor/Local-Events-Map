package com.gorman.network.data.datasource.mapevent

import com.gorman.network.data.models.MapEventRemote
import kotlinx.coroutines.flow.Flow

interface MapEventRemoteDataSource {
    fun getAllEvents(): Flow<List<MapEventRemote>>
    suspend fun getAllEventsOnce(): List<MapEventRemote>?
    suspend fun getSingleEvent(id: String): Result<MapEventRemote>
}

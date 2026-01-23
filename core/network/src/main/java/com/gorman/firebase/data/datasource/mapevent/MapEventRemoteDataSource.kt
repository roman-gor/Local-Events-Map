package com.gorman.firebase.data.datasource.mapevent

import com.gorman.firebase.data.models.MapEventRemote
import kotlinx.coroutines.flow.Flow

interface MapEventRemoteDataSource {
    fun getAllEvents(): Flow<List<MapEventRemote>>
    suspend fun getAllEventsOnce(): List<MapEventRemote>?
    suspend fun getSingleEvent(id: String): Result<MapEventRemote>
}

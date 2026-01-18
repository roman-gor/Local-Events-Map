package com.gorman.data.repository

import com.gorman.domainmodel.MapEvent
import kotlinx.coroutines.flow.Flow

interface IMapEventsRepository {
    fun getAllEvents(): Flow<List<MapEvent>>
    fun getEventsById(id: Long): Flow<MapEvent>
    fun getEventsByName(name: String): Flow<List<MapEvent>>
    suspend fun insertEvents(mapEvents: List<MapEvent>)
    suspend fun clearTable()
    fun getAllRemoteEvents(): Flow<List<MapEvent>>
    suspend fun syncWith()
}

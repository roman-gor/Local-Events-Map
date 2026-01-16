package com.gorman.database.domain.repository

import com.gorman.domainmodel.MapEvent
import kotlinx.coroutines.flow.Flow

interface MapEventsRepository {
    fun getAllEvents(): Flow<List<MapEvent>>
    fun getEventsById(id: Long): Flow<MapEvent>
    fun getEventsByName(name: String): Flow<List<MapEvent>>
    suspend fun insertEvents(mapEvents: List<MapEvent>)
    suspend fun clearTable()
}

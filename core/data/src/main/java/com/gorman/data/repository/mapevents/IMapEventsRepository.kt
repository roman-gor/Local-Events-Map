package com.gorman.data.repository.mapevents

import com.gorman.domainmodel.MapEvent
import kotlinx.coroutines.flow.Flow

interface IMapEventsRepository {
    fun getAllEvents(): Flow<List<MapEvent>>
    fun getEventById(id: Long): Flow<MapEvent>
    fun getEventsByName(name: String): Flow<List<MapEvent>>
    suspend fun syncWith(): Result<Unit>
    suspend fun isOutdated(): Boolean
}

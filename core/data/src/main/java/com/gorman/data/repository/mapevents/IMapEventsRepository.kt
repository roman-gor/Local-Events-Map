package com.gorman.data.repository.mapevents

import com.gorman.domainmodel.MapEvent
import kotlinx.coroutines.flow.Flow

interface IMapEventsRepository {
    fun getAllEvents(): Flow<List<MapEvent>>
    fun getEventById(id: String): Flow<MapEvent>
    fun getEventsByName(name: String): Flow<List<MapEvent>>
    suspend fun syncEventById(id: String): Result<Unit>
    suspend fun syncWith(): Result<Unit>
    fun isOutdated(): Flow<Boolean>
}

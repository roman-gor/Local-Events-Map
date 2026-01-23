package com.gorman.data.repository.mapevents

import com.gorman.domainmodel.MapEvent
import kotlinx.coroutines.flow.Flow

interface IMapEventsRepository {
    fun getAllEvents(): Flow<List<MapEvent>>
    fun getEventById(id: String): Flow<MapEvent>
    suspend fun updateFavouriteState(id: String): Result<Unit>
    fun getEventsByName(name: String): Flow<List<MapEvent>>
    suspend fun syncWith(): Result<Unit>
}

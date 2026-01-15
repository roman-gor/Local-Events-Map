package com.gorman.database.data.repository

import com.gorman.database.data.datasource.MapEventsDao
import com.gorman.database.domain.repository.MapEventsRepository
import com.gorman.database.toDomain
import com.gorman.database.toEntity
import com.gorman.domainmodel.MapEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MapEventsRepositoryImpl @Inject constructor(
    private val mapEventsDao: MapEventsDao
) : MapEventsRepository {
    override fun getAllEvents(): Flow<List<MapEvent>> {
        return mapEventsDao.getAllEvents().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getEventsById(id: Long): Flow<MapEvent> {
        return mapEventsDao.getEventById(id).map { it.toDomain() }
    }

    override fun getEventsByName(name: String): Flow<List<MapEvent>> {
        return mapEventsDao.getEventsByName(name).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertEvents(mapEvents: List<MapEvent>) {
        mapEventsDao.insertEvent(mapEvents.map { it.toEntity() })
    }

    override suspend fun clearTable() {
        mapEventsDao.clearTable()
    }
}

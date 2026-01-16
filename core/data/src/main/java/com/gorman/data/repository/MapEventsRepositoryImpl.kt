package com.gorman.data.repository

import com.gorman.database.data.datasource.MapEventsDao
import com.gorman.database.toDomain
import com.gorman.database.toEntity
import com.gorman.domainmodel.MapEvent
import com.gorman.firebase.data.datasource.MapEventRemoteDataSource
import com.gorman.firebase.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

internal class MapEventsRepositoryImpl @Inject constructor(
    private val mapEventsDao: MapEventsDao,
    private val mapEventRemoteDataSource: MapEventRemoteDataSource
) : IMapEventsRepository {

    override fun getAllLocalEvents(): Flow<List<MapEvent>> {
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

    override fun getAllRemoteEvents(): Flow<List<MapEvent>> {
        return mapEventRemoteDataSource.getAllEvents().map { events ->
            events.map { it.toDomain() }
        }
    }

    override suspend fun syncMapEvents() {
        getAllRemoteEvents()
            .onStart {
                clearTable()
            }
            .collect { events ->
                insertEvents(events)
            }
    }
}

package com.gorman.database.data.repository

import androidx.room.Transaction
import com.gorman.common.Event
import com.gorman.database.data.datasource.EventsDao
import com.gorman.database.domain.repository.EventsDbRepository
import com.gorman.database.toDomain
import com.gorman.database.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventsDbRepositoryImpl @Inject constructor(
    private val eventsDao: EventsDao
): EventsDbRepository {
    override fun getAllEvents(): Flow<List<Event>> {
        return eventsDao.getAllEvents().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getEventsById(id: Long): Flow<Event> {
        return eventsDao.getEventsById(id).map { it.toDomain() }
    }

    override fun getEventsByName(name: String): Flow<List<Event>> {
        return eventsDao.getEventsByName(name).map { list ->
            list.map { it.toDomain() }
        }
    }

    @Transaction
    override suspend fun updateEvents(events: List<Event>) {
        eventsDao.deleteDataAndResetId()
        eventsDao.insertEvents(events.map { it.toEntity() })
    }
}

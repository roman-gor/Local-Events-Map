package com.gorman.database.domain.repository

import com.gorman.common.Event
import kotlinx.coroutines.flow.Flow

interface EventsDbRepository {
    fun getAllEvents():  Flow<List<Event>>
    fun getEventsById(id: Long):  Flow<Event>
    fun getEventsByName(name: String):  Flow<List<Event>>
    suspend fun updateEvents(events: List<Event>)
}

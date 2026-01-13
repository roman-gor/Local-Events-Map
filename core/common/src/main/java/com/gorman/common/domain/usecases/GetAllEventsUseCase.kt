package com.gorman.common.domain.usecases

import com.gorman.database.domain.repository.EventsDbRepository
import com.gorman.domain_model.Event
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllEventsUseCase @Inject constructor(
    private val eventsDbRepository: EventsDbRepository
) {
    operator fun invoke(): Flow<List<Event>> =
        eventsDbRepository.getAllEvents()
}

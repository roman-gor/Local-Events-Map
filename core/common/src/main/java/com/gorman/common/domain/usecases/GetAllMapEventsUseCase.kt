package com.gorman.common.domain.usecases

import com.gorman.database.domain.repository.MapEventsRepository
import com.gorman.domain_model.MapEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMapEventsUseCase @Inject constructor(
    private val mapEventsRepository: MapEventsRepository
) {
    operator fun invoke(): Flow<List<MapEvent>> =
        mapEventsRepository.getAllEvents()
}

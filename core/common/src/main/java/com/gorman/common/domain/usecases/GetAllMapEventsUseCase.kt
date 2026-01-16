package com.gorman.common.domain.usecases

import com.gorman.database.domain.repository.MapEventsRepository
import com.gorman.domainmodel.MapEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMapEventsUseCase @Inject constructor(
    private val mapEventsRepository: MapEventsRepository
) {
    operator fun invoke(): Result<Flow<List<MapEvent>>> = runCatching {
        mapEventsRepository.getAllEvents()
    }
}

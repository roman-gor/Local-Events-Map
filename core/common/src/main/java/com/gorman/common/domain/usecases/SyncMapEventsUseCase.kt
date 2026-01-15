package com.gorman.common.domain.usecases

import com.gorman.database.domain.repository.MapEventsRepository
import com.gorman.firebase.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class SyncMapEventsUseCase @Inject constructor(
    private val mapEventsRepository: MapEventsRepository,
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke() {
        firebaseRepository.getAllEvents()
            .onStart {
                mapEventsRepository.clearTable()
            }
            .collect { events ->
                mapEventsRepository.insertEvent(events)
            }
    }
}

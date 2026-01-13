package com.gorman.common.domain.usecases

import android.util.Log
import com.gorman.database.domain.repository.EventsDbRepository
import com.gorman.firebase.domain.repository.FirebaseRepository
import javax.inject.Inject

class SyncEventsFromRemoteUseCase @Inject constructor(
    private val eventsDbRepository: EventsDbRepository,
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke() {
        firebaseRepository.getAllEvents().let { events ->
            Log.d("EventsUseCase", events.toString())
            eventsDbRepository.updateEvents(events)
        }
    }
}

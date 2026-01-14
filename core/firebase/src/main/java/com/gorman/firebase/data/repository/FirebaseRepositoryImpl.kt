package com.gorman.firebase.data.repository

import android.util.Log
import com.gorman.domain_model.MapEvent
import com.gorman.firebase.data.datasource.FirebaseApi
import com.gorman.firebase.domain.repository.FirebaseRepository
import com.gorman.firebase.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseApi: FirebaseApi
): FirebaseRepository {
    override fun getAllEvents(): Flow<List<MapEvent>> {
        Log.d("FirebaseRepository", "Запуск метода")
        return firebaseApi.getAllEvents().map { events ->
            events.map { it.toDomain() }
        }
    }

    override suspend fun getSingleEvent(id: String): MapEvent {
        return firebaseApi.getSingleEvent(id).getOrThrow().toDomain()
    }
}

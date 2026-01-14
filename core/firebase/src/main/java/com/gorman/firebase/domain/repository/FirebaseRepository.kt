package com.gorman.firebase.domain.repository

import com.gorman.domain_model.MapEvent
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    fun getAllEvents(): Flow<List<MapEvent>>
    suspend fun getSingleEvent(id: String): MapEvent
}

package com.gorman.firebase.data.datasource

import com.gorman.firebase.data.models.MapEventFirebase
import kotlinx.coroutines.flow.Flow

interface MapEventRemoteDataSource {
    fun getAllEvents(): Flow<List<MapEventFirebase>>
    suspend fun getAllEventsOnce(): List<MapEventFirebase>?
    suspend fun getSingleEvent(id: String): Result<MapEventFirebase>
}

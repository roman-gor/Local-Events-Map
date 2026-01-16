package com.gorman.firebase.data.repository

import android.util.Log
import com.gorman.domainmodel.MapEvent
import com.gorman.firebase.data.datasource.MapEventRemoteDataSource
import com.gorman.firebase.domain.repository.FirebaseRepository
import com.gorman.firebase.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val mapEventRemoteDataSource: MapEventRemoteDataSource
) : FirebaseRepository {
    override fun getAllEvents(): Flow<List<MapEvent>> {
        Log.d("FirebaseRepository", "Starting method")
        return mapEventRemoteDataSource.getAllEvents().map { events ->
            events.map { it.toDomain() }
        }
    }

    override suspend fun getSingleEvent(id: String): MapEvent {
        return mapEventRemoteDataSource.getSingleEvent(id).getOrThrow().toDomain()
    }
}

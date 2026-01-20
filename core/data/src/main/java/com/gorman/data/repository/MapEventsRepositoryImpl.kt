package com.gorman.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.gorman.database.data.datasource.MapEventsDao
import com.gorman.database.data.datasource.MapEventsDatabase
import com.gorman.database.toDomain
import com.gorman.database.toEntity
import com.gorman.domainmodel.MapEvent
import com.gorman.firebase.data.datasource.MapEventRemoteDataSource
import com.gorman.firebase.toDomain
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

internal class MapEventsRepositoryImpl @Inject constructor(
    private val mapEventsDao: MapEventsDao,
    private val mapEventRemoteDataSource: MapEventRemoteDataSource,
    private val database: MapEventsDatabase
) : IMapEventsRepository {

    override fun getAllEvents(): Flow<List<MapEvent>> {
        return mapEventsDao.getAllEvents().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getEventById(id: Long): Flow<MapEvent> {
        return mapEventsDao.getEventById(id).map { it.toDomain() }
    }

    override fun getEventsByName(name: String): Flow<List<MapEvent>> {
        return mapEventsDao.getEventsByName(name).map { list ->
            list.map { it.toDomain() }
        }
    }

    private suspend fun getAllRemoteEvents(): List<MapEvent>? {
        return mapEventRemoteDataSource.getAllEventsOnce()?.map { event ->
            event.toDomain()
        }
    }

    override suspend fun syncWith(): Result<Unit> {
        return try {
            val remoteEvents = getAllRemoteEvents()
            if (remoteEvents != null) {
                val entities = remoteEvents.map { it.toEntity() }
                val remoteIds = entities.map { it.id }
                database.withTransaction {
                    if (remoteIds.isNotEmpty()) {
                        mapEventsDao.deleteEventsNotIn(remoteIds)
                        mapEventsDao.upsertEvent(entities)
                    } else {
                        mapEventsDao.clearAll()
                    }
                }
                Result.success(Unit)
            } else {
                Result.failure(IOException("Error network connection"))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e("Repository", "Sync failed ${e.message}")
            Result.failure(e)
        } catch (e: IOException) {
            Log.e("Repository", "Sync failed ${e.message}")
            Result.failure(e)
        }
    }
}

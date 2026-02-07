package com.gorman.data.repository.mapevents

import android.util.Log
import androidx.room.withTransaction
import com.gorman.cache.data.DataStoreManager
import com.gorman.database.data.datasource.LocalEventsDatabase
import com.gorman.database.data.datasource.dao.MapEventsDao
import com.gorman.database.mappers.toDomain
import com.gorman.database.mappers.toEntity
import com.gorman.domainmodel.MapEvent
import com.gorman.network.data.datasource.mapevent.MapEventRemoteDataSource
import com.gorman.network.mappers.toDomain
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private const val TTL_MS = 24 * 60 * 60 * 1000L

internal class MapEventsRepository @Inject constructor(
    private val mapEventsDao: MapEventsDao,
    private val mapEventRemoteDataSource: MapEventRemoteDataSource,
    private val database: LocalEventsDatabase,
    private val dataStoreManager: DataStoreManager
) : IMapEventsRepository {

    override fun getAllEvents(): Flow<List<MapEvent>> {
        return mapEventsDao.getAllEvents().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getEventById(id: String): Flow<MapEvent> {
        return mapEventsDao.getEventById(id).map { it.toDomain() }
    }

    override fun getEventsByName(name: String): Flow<List<MapEvent>> {
        return mapEventsDao.getEventsByName(name).map { list ->
            list.map { it.toDomain() }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun syncEventById(id: String): Result<Unit> {
        return try {
            val remoteEventResult = mapEventRemoteDataSource.getSingleEvent(id)
            remoteEventResult.mapCatching { remoteEvent ->
                mapEventsDao.upsertEvent(listOf(remoteEvent.toDomain().toEntity()))
            }
        } catch (e: Exception) {
            Log.e("Repository", "Failed to fetch event $id: ${e.message}")
            Result.failure(e)
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
                dataStoreManager.saveSyncTimestamp(System.currentTimeMillis())
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

    override suspend fun isOutdated(): Boolean {
        val lastSyncTime = dataStoreManager.lastSyncTimestamp.first()
        val currentTime = System.currentTimeMillis()
        return lastSyncTime?.let { (currentTime - it) > TTL_MS } == true
    }
}

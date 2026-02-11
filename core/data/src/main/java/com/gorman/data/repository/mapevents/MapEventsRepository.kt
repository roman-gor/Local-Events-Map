package com.gorman.data.repository.mapevents

import androidx.room.withTransaction
import com.gorman.cache.data.DataStoreManager
import com.gorman.database.data.datasource.MapEventsDao
import com.gorman.database.data.datasource.MapEventsDatabase
import com.gorman.database.toDomain
import com.gorman.database.toEntity
import com.gorman.domainmodel.MapEvent
import com.gorman.firebase.data.datasource.MapEventRemoteDataSource
import com.gorman.firebase.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.time.ExperimentalTime

private const val TTL_MS = 24 * 60 * 60 * 1000L

internal class MapEventsRepository @Inject constructor(
    private val mapEventsDao: MapEventsDao,
    private val mapEventRemoteDataSource: MapEventRemoteDataSource,
    private val database: MapEventsDatabase,
    private val dataStoreManager: DataStoreManager
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

    override suspend fun syncWith(): Result<Unit> = runCatching {
        val remoteEvents = getAllRemoteEvents()
        return if (remoteEvents != null) {
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
    }

    @OptIn(ExperimentalTime::class)
    override fun isOutdated(): Flow<Boolean> =
        dataStoreManager.lastSyncTimestamp.map { lastSyncTime ->
            val currentZone = ZoneId.systemDefault()
            val currentTime = ZonedDateTime.now(currentZone).toEpochSecond()
            lastSyncTime?.let { (currentTime - it) > TTL_MS } == true
        }
}

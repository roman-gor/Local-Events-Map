package com.gorman.database.data.datasource

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gorman.database.data.model.MapEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MapEventsDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<MapEventEntity>>

    @Upsert
    suspend fun upsertEvent(events: List<MapEventEntity>)

    @Query("SELECT * FROM events WHERE id=:id")
    fun getEventById(id: String): Flow<MapEventEntity>

    @Query("UPDATE events SET isFavourite = NOT isFavourite WHERE id = :id")
    suspend fun toggleFavouriteState(id: String): Int

    @Query("SELECT * FROM events WHERE name LIKE '%' || :name || '%'")
    fun getEventsByName(name: String): Flow<List<MapEventEntity>>

    @Query("DELETE FROM events WHERE id NOT IN (:remoteIds)")
    suspend fun deleteEventsNotIn(remoteIds: List<String>)

    @Query("DELETE FROM events")
    suspend fun clearAll()
}

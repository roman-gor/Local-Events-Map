package com.gorman.database.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.gorman.database.data.model.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<EventEntity>>
    @Insert(onConflict = REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)
    @Query("SELECT * FROM events WHERE id=:id")
    fun getEventsById(id: Long): Flow<EventEntity>
    @Query("SELECT * FROM events WHERE name LIKE '%' || :name || '%'")
    fun getEventsByName(name: String): Flow<List<EventEntity>>
    @Query("DELETE FROM events")
    suspend fun deleteAll()
    @Query("DELETE FROM sqlite_sequence WHERE name = 'events'")
    suspend fun resetTableSequence()
    @Transaction
    suspend fun deleteDataAndResetId() {
        deleteAll()
        resetTableSequence()
    }
}

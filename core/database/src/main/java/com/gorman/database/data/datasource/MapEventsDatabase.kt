package com.gorman.database.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gorman.database.data.model.MapEventEntity

@Database(
    entities = [
        MapEventEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MapEventsDatabase : RoomDatabase() {
    abstract fun mapEventsDao(): MapEventsDao
}

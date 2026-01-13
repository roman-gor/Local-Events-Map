package com.gorman.database.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gorman.database.data.datasource.EventsDao
import com.gorman.database.data.model.EventEntity

@Database(entities = [
    EventEntity::class
], version = 1, exportSchema = false)
abstract class EventDatabase: RoomDatabase() {
    abstract fun eventsDao(): EventsDao
}

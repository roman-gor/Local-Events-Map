package com.gorman.database.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gorman.database.data.datasource.dao.BookmarkDao
import com.gorman.database.data.datasource.dao.MapEventsDao
import com.gorman.database.data.datasource.dao.UserDataDao
import com.gorman.database.data.model.MapEventEntity
import com.gorman.database.data.model.UserDataEntity

@Database(
    entities = [
        MapEventEntity::class,
        UserDataEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LocalEventsDatabase : RoomDatabase() {
    abstract fun mapEventsDao(): MapEventsDao
    abstract fun userDataDao(): UserDataDao
    abstract fun bookmarkDao(): BookmarkDao
}

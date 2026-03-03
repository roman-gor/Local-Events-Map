package com.gorman.database.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gorman.database.data.datasource.dao.BookmarkDao
import com.gorman.database.data.datasource.dao.BookmarkMapEventDao
import com.gorman.database.data.datasource.dao.MapEventsDao
import com.gorman.database.data.datasource.dao.SettingsDao
import com.gorman.database.data.datasource.dao.UserDataDao
import com.gorman.database.data.model.BookmarkDataEntity
import com.gorman.database.data.model.MapEventEntity
import com.gorman.database.data.model.SettingsEntity
import com.gorman.database.data.model.UserDataEntity
import com.gorman.database.utils.CityDataConverter
import com.gorman.database.utils.FiltersConverter

@Database(
    entities = [
        MapEventEntity::class,
        UserDataEntity::class,
        BookmarkDataEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    value = [
        CityDataConverter::class,
        FiltersConverter::class
    ]
)
abstract class LocalEventsDatabase : RoomDatabase() {
    abstract fun mapEventsDao(): MapEventsDao
    abstract fun userDataDao(): UserDataDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun bookmarkMapEventDao(): BookmarkMapEventDao
    abstract fun settingsDao(): SettingsDao
}

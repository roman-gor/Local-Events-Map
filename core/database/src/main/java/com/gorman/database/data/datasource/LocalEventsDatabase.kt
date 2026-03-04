package com.gorman.database.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gorman.database.data.datasource.dao.BookmarkDao
import com.gorman.database.data.datasource.dao.BookmarkMapEventDao
import com.gorman.database.data.datasource.dao.MapEventsDao
import com.gorman.database.data.datasource.dao.UserCitySettingsDao
import com.gorman.database.data.datasource.dao.UserDataDao
import com.gorman.database.data.datasource.dao.UserFiltersDao
import com.gorman.database.data.model.BookmarkDataEntity
import com.gorman.database.data.model.MapEventEntity
import com.gorman.database.data.model.UserCitySettingsEntity
import com.gorman.database.data.model.UserDataEntity
import com.gorman.database.data.model.UserFiltersEntity
import com.gorman.database.utils.FiltersCategoriesConverter

@Database(
    entities = [
        MapEventEntity::class,
        UserDataEntity::class,
        UserFiltersEntity::class,
        UserCitySettingsEntity::class,
        BookmarkDataEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(FiltersCategoriesConverter::class)
abstract class LocalEventsDatabase : RoomDatabase() {
    abstract fun mapEventsDao(): MapEventsDao
    abstract fun userDataDao(): UserDataDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun bookmarkMapEventDao(): BookmarkMapEventDao
    abstract fun userFiltersDao(): UserFiltersDao
    abstract fun userCitySettingsDao(): UserCitySettingsDao
}

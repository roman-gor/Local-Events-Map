package com.gorman.database.di

import android.content.Context
import androidx.room.Room
import com.gorman.database.data.datasource.LocalEventsDatabase
import com.gorman.database.data.datasource.dao.BookmarkDao
import com.gorman.database.data.datasource.dao.MapEventsDao
import com.gorman.database.data.datasource.dao.UserDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideEventDatabase(@ApplicationContext context: Context): LocalEventsDatabase =
        Room.databaseBuilder(
            context,
            LocalEventsDatabase::class.java,
            "events_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    @Singleton
    fun provideEventsDao(db: LocalEventsDatabase): MapEventsDao = db.mapEventsDao()

    @Provides
    @Singleton
    fun provideUserDataDao(db: LocalEventsDatabase): UserDataDao = db.userDataDao()

    @Provides
    @Singleton
    fun provideBookmarkDao(db: LocalEventsDatabase): BookmarkDao = db.bookmarkDao()
}

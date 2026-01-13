package com.gorman.database.di

import android.content.Context
import androidx.room.Room
import com.gorman.database.data.datasource.EventDatabase
import com.gorman.database.data.datasource.EventsDao
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
    fun provideEventDatabase(@ApplicationContext context: Context): EventDatabase =
        Room.databaseBuilder(context,
            EventDatabase::class.java,
            "events_db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    @Singleton
    fun provideEventsDao(db: EventDatabase): EventsDao = db.eventsDao()
}

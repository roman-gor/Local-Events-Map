package com.gorman.database.di

import android.content.Context
import androidx.room.Room
import com.gorman.database.data.datasource.MapEventsDao
import com.gorman.database.data.datasource.MapEventsDatabase
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
    fun provideEventDatabase(@ApplicationContext context: Context): MapEventsDatabase =
        Room.databaseBuilder(
            context,
            MapEventsDatabase::class.java,
            "events_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    @Singleton
    fun provideEventsDao(db: MapEventsDatabase): MapEventsDao = db.mapEventsDao()
}

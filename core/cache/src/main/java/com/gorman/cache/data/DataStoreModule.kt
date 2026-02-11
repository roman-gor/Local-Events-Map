package com.gorman.cache.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    private const val LOCAL_EVENTS_DATASTORE_NAME = "settings"
    private val Context.localEventsMapDataStore: DataStore<Preferences> by preferencesDataStore(
        name = LOCAL_EVENTS_DATASTORE_NAME
    )

    @Provides
    @Singleton
    fun provideLocalEventsMapDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.localEventsMapDataStore
}

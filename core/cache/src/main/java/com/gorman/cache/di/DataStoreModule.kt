package com.gorman.cache.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.gorman.cache.data.PreferencesDataSource
import com.gorman.data.cache.IPreferencesDataSource
import dagger.Binds
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

@Module
@InstallIn(SingletonComponent::class)
internal interface CacheModule {
    @Binds
    fun bindPreferencesDataSource(impl: PreferencesDataSource): IPreferencesDataSource
}

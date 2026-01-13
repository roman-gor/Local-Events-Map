package com.gorman.database.di

import com.gorman.database.data.repository.EventsDbRepositoryImpl
import com.gorman.database.domain.repository.EventsDbRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindEventsDbRepository(impl: EventsDbRepositoryImpl): EventsDbRepository
}

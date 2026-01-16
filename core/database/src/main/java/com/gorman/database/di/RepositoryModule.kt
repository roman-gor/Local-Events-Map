package com.gorman.database.di

import com.gorman.database.data.repository.MapEventsRepositoryImpl
import com.gorman.database.domain.repository.MapEventsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindMapEventRepository(impl: MapEventsRepositoryImpl): MapEventsRepository
}

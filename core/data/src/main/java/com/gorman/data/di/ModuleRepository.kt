package com.gorman.data.di

import com.gorman.data.repository.GeoRepositoryImpl
import com.gorman.data.repository.IGeoRepository
import com.gorman.data.repository.IMapEventsRepository
import com.gorman.data.repository.MapEventsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ModuleRepository {
    @Binds
    @Singleton
    fun bindMapEventRepository(impl: MapEventsRepositoryImpl): IMapEventsRepository

    @Binds
    @Singleton
    fun bindGeoRepository(impl: GeoRepositoryImpl): IGeoRepository
}

package com.gorman.data.di

import com.gorman.data.repository.geo.GeoRepositoryImpl
import com.gorman.data.repository.geo.IGeoRepository
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.data.repository.mapevents.MapEventsRepositoryImpl
import com.gorman.data.repository.user.IUserRepository
import com.gorman.data.repository.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ModuleRepository {
    @Binds
    fun bindMapEventRepository(impl: MapEventsRepositoryImpl): IMapEventsRepository

    @Binds
    fun bindGeoRepository(impl: GeoRepositoryImpl): IGeoRepository

    @Binds
    fun bindUserRepository(impl: UserRepositoryImpl): IUserRepository
}

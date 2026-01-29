package com.gorman.data.di

import com.gorman.data.repository.geo.GeoRepository
import com.gorman.data.repository.geo.IGeoRepository
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.data.repository.mapevents.MapEventsRepository
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ModuleRepository {
    @Binds
    fun bindMapEventRepository(impl: MapEventsRepository): IMapEventsRepository

    @Binds
    fun bindGeoRepository(impl: GeoRepository): IGeoRepository
}

@Module
@InstallIn(SingletonComponent::class)
object SearchManagerModule {
    @Provides
    fun provideSearchManager(): SearchManager =
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
}

package com.gorman.map.di

import com.gorman.map.mapmanager.IMapManager
import com.gorman.map.mapmanager.MapManager
import com.gorman.map.search.IMapSearchManager
import com.gorman.map.search.MapSearchManager
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object YandexModule {
    @Provides
    fun provideMapKit(): MapKit = MapKitFactory.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
interface MapModule {
    @Binds
    fun bindMapManager(impl: MapManager): IMapManager

    @Binds
    fun bindMapSearchManager(impl: MapSearchManager): IMapSearchManager
}

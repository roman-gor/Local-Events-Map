package com.gorman.feature.events.impl.di

import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MapKitModule {
    @Provides
    fun provideMapKit(): MapKit = MapKitFactory.getInstance()
}

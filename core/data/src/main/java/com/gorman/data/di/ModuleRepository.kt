package com.gorman.data.di

import com.gorman.data.repository.database.IMapEventsLocalRepository
import com.gorman.data.repository.database.MapEventsLocalRepositoryImpl
import com.gorman.data.repository.network.IMapEventRemoteRepository
import com.gorman.data.repository.network.MapEventRemoteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


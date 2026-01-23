package com.gorman.firebase.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gorman.firebase.data.datasource.mapevent.MapEventRemoteDataSource
import com.gorman.firebase.data.datasource.mapevent.MapEventRemoteDataSourceImpl
import com.gorman.firebase.data.datasource.users.IUserRemoteDataSource
import com.gorman.firebase.data.datasource.users.UserRemoteDataSourceImpl
import com.gorman.firebase.data.models.FirebaseConstants
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FirebaseRepositoryModule {
    @Binds
    @Singleton
    fun bindMapEventDataSource(impl: MapEventRemoteDataSourceImpl): MapEventRemoteDataSource

    @Binds
    @Singleton
    fun bindUserDataSource(impl: UserRemoteDataSourceImpl): IUserRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseMainModule {
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    fun provideDatabaseReference(database: FirebaseDatabase): DatabaseReference {
        return database.getReference(FirebaseConstants.ROOT_PATH.value)
    }
}

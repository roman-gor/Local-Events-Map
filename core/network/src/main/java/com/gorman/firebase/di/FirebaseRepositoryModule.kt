package com.gorman.firebase.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gorman.firebase.data.datasource.MapEventRemoteDataSource
import com.gorman.firebase.data.datasource.MapEventRemoteDataSourceImpl
import com.gorman.firebase.data.models.FirebaseConstants
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseRepositoryModule {
    @Binds
    abstract fun bindFirebaseApi(impl: MapEventRemoteDataSourceImpl): MapEventRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseMainModule {
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    fun provideDatabaseReference(database: FirebaseDatabase): DatabaseReference {
        return database.getReference(FirebaseConstants.EVENTS_PATH.value)
    }
}

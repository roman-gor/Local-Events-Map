package com.gorman.firebase.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gorman.firebase.data.datasource.FirebaseApi
import com.gorman.firebase.data.datasource.FirebaseApiImpl
import com.gorman.firebase.data.models.FirebaseConstants
import com.gorman.firebase.data.repository.FirebaseRepositoryImpl
import com.gorman.firebase.domain.repository.FirebaseRepository
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
    @Singleton
    abstract fun bindFirebaseApi(impl: FirebaseApiImpl): FirebaseApi

    @Binds
    @Singleton
    abstract fun bindFirebaseRepository(impl: FirebaseRepositoryImpl): FirebaseRepository
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
        return database.getReference(FirebaseConstants.EVENTS_PATH.value)
    }
}

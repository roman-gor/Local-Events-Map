package com.gorman.auth.di

import com.google.firebase.auth.FirebaseAuth
import com.gorman.auth.data.Authenticator
import com.gorman.auth.data.IAuthenticator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
internal interface AuthRepositoryModule {
    @Binds
    fun bindAuthenticator(impl: Authenticator): IAuthenticator
}

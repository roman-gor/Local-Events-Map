package com.gorman.navigation.di

import com.gorman.navigation.navigator.AppNavigator
import com.gorman.navigation.navigator.IAppNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigatorModule {
    @Binds
    @Singleton
    fun bindAppNavigator(impl: AppNavigator): IAppNavigator
}

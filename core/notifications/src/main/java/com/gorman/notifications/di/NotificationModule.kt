package com.gorman.notifications.di

import com.google.firebase.messaging.FirebaseMessaging
import com.gorman.notifications.notificator.INotificationTokenDataSource
import com.gorman.notifications.notificator.NotificationTokenDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Provides
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
interface NotificatorModule {
    @Binds
    fun bindNotificator(impl: NotificationTokenDataSource): INotificationTokenDataSource
}

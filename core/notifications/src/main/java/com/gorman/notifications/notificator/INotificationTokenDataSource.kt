package com.gorman.notifications.notificator

interface INotificationTokenDataSource {
    suspend fun getNotificationToken(): String
}

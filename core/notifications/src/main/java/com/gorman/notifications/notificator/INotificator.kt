package com.gorman.notifications.notificator

interface INotificator {
    suspend fun getUserToken(): String
}

package com.gorman.notifications.notificator

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationTokenDataSource @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
) : INotificationTokenDataSource {
    override suspend fun getNotificationToken(): String =
        firebaseMessaging.token.await()
}

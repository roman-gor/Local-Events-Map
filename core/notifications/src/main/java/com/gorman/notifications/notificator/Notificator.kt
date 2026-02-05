package com.gorman.notifications.notificator

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class Notificator @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
) : INotificator {
    override suspend fun getUserToken(): String =
        firebaseMessaging.token.await()
}

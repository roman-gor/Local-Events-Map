package com.gorman.localeventsmap.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gorman.cache.data.IPreferencesDataSource
import com.gorman.data.repository.user.IUserRepository
import com.gorman.localeventsmap.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: IUserRepository

    @Inject
    lateinit var preferencesDataSource: IPreferencesDataSource

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        val title = data["title"] ?: getString(R.string.notificationTitle)
        val body = getString(R.string.notificationText)
        val eventId = data["eventId"]

        Log.d("FCM_DEBUG", "Parsed: title=$title, body=$body, id=$eventId")

        if (eventId != null) {
            sendNotifications(title, body, eventId)
        }
    }

    private fun sendNotifications(title: String, body: String, eventId: String) {
        val intent = Intent(Intent.ACTION_VIEW, "app://events/$eventId".toUri()).apply {
            `package` = packageName
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            eventId.hashCode(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "new_events_channel"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_bell)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "New events",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(eventId.hashCode(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        preferencesDataSource.currentUid
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { uid ->
                userRepository.saveTokenToUser(uid, token)
            }
            .launchIn(CoroutineScope(Dispatchers.IO))

        Log.d("FCM_TOKEN", "New token: $token")
    }
}

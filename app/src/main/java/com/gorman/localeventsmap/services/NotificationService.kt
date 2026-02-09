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
import com.gorman.data.repository.user.IUserRepository
import com.gorman.database.data.datasource.dao.UserDataDao
import com.gorman.localeventsmap.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: IUserRepository

    @Inject
    lateinit var userDataDao: UserDataDao

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

    @Suppress("TooGenericExceptionCaught")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            val uid = userDataDao.getUser().map { it?.uid }.firstOrNull()

            if (!uid.isNullOrBlank()) {
                try {
                    userRepository.saveTokenToUser(uid, token)
                    Log.d("FCM_TOKEN", "Token updated on server for user $uid")
                } catch (e: Exception) {
                    Log.e("FCM_TOKEN", "Failed to update token on server", e)
                }
            }
        }
        Log.d("FCM_TOKEN", "New token: $token")
    }
}

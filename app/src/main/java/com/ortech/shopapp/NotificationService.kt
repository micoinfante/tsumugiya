package com.ortech.shopapp

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        Log.d("Notification Data", "Notif From: ${p0.from}")
        if (p0.data.isNotEmpty()) {
            Log.d("Notification Data", "Data Payload: ${p0.data}")
        }

        if (p0.notification != null) {
            Log.d("Notif Body: ", p0.notification!!.toString())
            sendNotification(p0)
        }
        sendNotification(p0)
        super.onMessageReceived(p0)
    }

    override fun onNewToken(p0: String) {
        Log.d("Notification Token", "Refreshed Token: $p0")
    }

    @SuppressLint("ServiceCast")
    private fun sendNotification(remoteMessage: RemoteMessage) {
        Log.d("NotificationService","Showing Notification. ")
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
            .setContentText(remoteMessage.notification?.body)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("NotificationService","Notification Presented")
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }


        companion object {
        private const val Service = "Notification Service"
        private const val Data = "Notification Data"


    }

}

package com.ortech.shopapp

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ortech.shopapp.Models.UserSingleton

class NotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.d("Notification Data", "Notif From: ${p0.from}")
        if (p0.data.isNotEmpty()) {
            Log.d("Notification Data", "Data Payload: ${p0.data}")
        }

        if (p0.notification != null) {
            Log.d("Notif Body: ", p0.notification!!.toString())
            sendNotification(p0)
        }
        sendNotification(p0)

        val intent = Intent(this@NotificationService, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onNewToken(p0: String) {
        Log.d("Notification Token", "Refreshed Token: $p0")
        val db = Firebase.firestore
        val sharedPreferences = getSharedPreferences(
            UserSingleton.instance.userID, Context.MODE_PRIVATE) ?: return
        sharedPreferences.getString(UserSingleton.instance.userID, null)
            .let { userUUID ->
                if (userUUID != null) {
                    UserSingleton.instance.setCurrentUserID(userUUID)
                    FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener{task ->
                            if (!task.isSuccessful) {
                                Log.w("Firebase Instance", "getInstanceId failed", task.exception)
                            }

                            val token = task.result?.token
                            val data = hashMapOf("token" to token)
                            UserSingleton.instance.fcmToken = token!!
                            db.collection("GlobalUsers").document(userUUID).set(data, SetOptions.merge())
                        }

                }
            }

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

    private fun handleMessage(remoteMessage: RemoteMessage) {
        //1
        val handler = Handler(Looper.getMainLooper())

        //2
        handler.post(Runnable {
            Toast.makeText(baseContext, "${remoteMessage.notification?.title}",
                Toast.LENGTH_LONG).show()
        }
        )
    }


        companion object {
        private const val Service = "Notification Service"
        private const val Data = "Notification Data"


    }

}

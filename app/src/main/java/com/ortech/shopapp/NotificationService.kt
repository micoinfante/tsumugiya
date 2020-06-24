package com.ortech.shopapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ortech.shopapp.Models.UserSingleton
import java.text.SimpleDateFormat
import java.util.*


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

//        val intent = Intent(this@NotificationService, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
  }

  override fun onNewToken(p0: String) {
    Log.d("Notification Token", "Refreshed Token: $p0")
    val db = Firebase.firestore
    val userUUID = UserSingleton.instance.userID
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

  @SuppressLint("ServiceCast")

  private fun sendNotification(remoteMessage: RemoteMessage) {
    val notificationID = createID()
    Log.d("NotificationService","Showing Notification. $notificationID")
    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pendingIntent = PendingIntent.getActivity(
      this, 9991, intent,
      PendingIntent.FLAG_ONE_SHOT
    )
    val channelId = "Channel-ID"
    val channelName = "AppName"
    val importance = NotificationManager.IMPORTANCE_HIGH
    val notificationManager =
      getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val mChannel = NotificationChannel(
        channelId, channelName, importance
      )
      notificationManager.createNotificationChannel(mChannel)
    }

    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val notificationBuilder = NotificationCompat.Builder(this, channelId)
      .setContentText(remoteMessage.notification?.body)
      .setContentTitle(remoteMessage.notification?.title)
      .setAutoCancel(true)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setSound(defaultSoundUri)
      .setLights(Color.YELLOW, 500, 5000)
      .setContentIntent(pendingIntent)

    val stackBuilder = TaskStackBuilder.create(this)
    stackBuilder.addNextIntent(intent)
    val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    notificationBuilder.setContentIntent(resultPendingIntent)

    Log.d("NotificationService","Notification Presented")
    notificationManager.notify(notificationID, notificationBuilder.build())
  }

  private fun createID(): Int {
    val now = Date()
    val sdf = SimpleDateFormat("ddHHmmss", Locale.getDefault()).format(now)
    return sdf.toInt()
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

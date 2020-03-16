package com.ortech.shopapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val intent = Intent(this, BottomNavigationActivity::class.java)
//        startActivity(intent)
        var builder = NotificationCompat.Builder(this, 999.toString())
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setContentTitle("This is a test title")
            .setContentText("This is a test content")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val intent = Intent(this, HomeScreen::class.java)
        startActivity(intent)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Fir Instance", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
//                val msg = getString("Got Firebase token: ", token)
                Log.d("Firebase Token", token!!)
                Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            })
    }
}

package com.ortech.shopapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat

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
    }
}

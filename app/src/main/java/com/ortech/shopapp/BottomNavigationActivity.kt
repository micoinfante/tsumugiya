package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class BottomNavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

//        val navController: NavController = findNavController(R.id.nav_host_fragment)
        val navControllerFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = navControllerFragment?.findNavController()
        Log.d("HomeScreen", "Fragment: $navControllerFragment Controller: $navController")
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
//        supportActionBar?.hide()
        setupActionBarWithNavController(navController!!, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}

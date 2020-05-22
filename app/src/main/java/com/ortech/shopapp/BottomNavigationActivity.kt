package com.ortech.shopapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ortech.shopapp.ui.dashboard.DashboardFragment
import com.ortech.shopapp.ui.home.HomeFragment
import kotlinx.android.synthetic.main.activity_bottom_navigation.*

class BottomNavigationActivity : AppCompatActivity() {

  private val homeScreenTab = HomeScreen()
  private val customerTab = CustomerQRCodeActivity()
  private val storeTab = StoreTabListActivity()
  private val menuTab= MenuFragment()
  private val instagramTab = InstagramFragment()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_bottom_navigation)
    val navView: BottomNavigationView = findViewById(R.id.nav_view)

    val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
    ActivityCompat.requestPermissions(this, permissions,0)

    loadFragment(homeScreenTab)
    navView.setOnNavigationItemSelectedListener {
      when(it.itemId){
        R.id.navigation_home -> {
          loadFragment(homeScreenTab)
          return@setOnNavigationItemSelectedListener true
        }

        R.id.navigation_customer_qr -> {
          loadFragment(customerTab)
          return@setOnNavigationItemSelectedListener true
        }
        R.id.navigation_store_tabs -> {
          loadFragment(storeTab)
          return@setOnNavigationItemSelectedListener true
        }
        R.id.navigation_menu_category -> {
          loadFragment(menuTab)
          return@setOnNavigationItemSelectedListener true
        }
        R.id.navigation_instagram -> {
          loadFragment(instagramTab)
          return@setOnNavigationItemSelectedListener true
        }

      }
      false
    }
  }

  private fun loadFragment(fragment: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.container, fragment)
    transaction.addToBackStack(null)
    transaction.commit()
  }
}

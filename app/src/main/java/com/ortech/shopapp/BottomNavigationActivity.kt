package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_bottom_navigation)
    val navView: BottomNavigationView = findViewById(R.id.nav_view)
//
////        val navController: NavController = findNavController(R.id.nav_host_fragment)
//    val navControllerFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//    val navController = navControllerFragment?.findNavController()
//    Log.d("HomeScreen", "Fragment: $navControllerFragment Controller: $navController")
//    // Passing each menu ID as a set of Ids because each
//    // menu should be considered as top level destinations.
//    val appBarConfiguration = AppBarConfiguration(
//      setOf(
//        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_home
//      )
//    )
////        supportActionBar?.hide()
//    setupActionBarWithNavController(navController!!, appBarConfiguration)
//    navView.setupWithNavController(navController)
    loadFragment(HomeFragment())
    navView.setOnNavigationItemSelectedListener {
      when(it.itemId){
        R.id.navigation_home-> {
          loadFragment(HomeFragment())
          return@setOnNavigationItemSelectedListener true
        }

        R.id.navigation_dashboard-> {
          loadFragment(DashboardFragment())
          return@setOnNavigationItemSelectedListener true
        }
        R.id.navigation_notifications-> {
          loadFragment(StoreListFragment())
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

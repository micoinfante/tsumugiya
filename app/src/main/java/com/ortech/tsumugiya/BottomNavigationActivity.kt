package com.ortech.tsumugiya

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ortech.tsumugiya.ui.dashboard.DashboardFragment
import com.ortech.tsumugiya.ui.home.HomeFragment
import kotlinx.android.synthetic.main.activity_bottom_navigation.*

class BottomNavigationActivity : AppCompatActivity(){

  private val homeScreenTab = HomeScreen()
  private val customerTab = CustomerQRCodeActivity()
  private val storeTab = StoreTabListActivity()
  private val menuTab= MenuFragment()
  private val instagramTab = InstagramFragment()
  lateinit var navView: BottomNavigationView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_bottom_navigation)
    navView = findViewById(R.id.nav_view)

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

      }
      false
    }
  }

  private fun loadFragment(fragment: Fragment) {
    selectContentFragment(fragment)
  }

   fun selectContentFragment(fragmentToSelect: Fragment, useAnimations: Boolean = false) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()

    if (useAnimations) {
      fragmentTransaction.setCustomAnimations(
          R.anim.enter_from_right,
          R.anim.exit_to_left,
          R.anim.enter_from_left,
          R.anim.exit_to_right
      )
    }

    val currentFragments = supportFragmentManager.fragments
    if (currentFragments.contains(fragmentToSelect)) {
      // Show the fragment that we want to be selected.
      if (fragmentToSelect is CustomerQRCodeActivity) {
        fragmentToSelect.onResume()
      }
      fragmentTransaction.show(fragmentToSelect)
    } else {
      // The fragment to be selected does not (yet) exist in the fragment manager, add it.
        fragmentTransaction.add(R.id.container, fragmentToSelect)


    }
    // Iterate through all cached fragments.
    for (cachedFragment in currentFragments) {
      if (cachedFragment !== fragmentToSelect) {
        // Hide the fragments that are not the one being selected.
        // Uncomment following line and change the name of the fragment if your host isn't an activity
        // and a fragment otherwise whole view will get hidden.
        // if (!cachedFragment.toString().contains("HomeContainerFragment"))

        fragmentTransaction.hide(cachedFragment)
      }
    }
    fragmentTransaction.commit()
  }

  override fun onBackPressed() {
    return
  }



}

package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.tabs.TabLayout
import com.ortech.shopapp.ui.main.SectionsPagerAdapter

class StoreTabListActivity : Fragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_store_tab_list)
    val view = inflater.inflate(R.layout.activity_store_tab_list, container, false)
    val sectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)
    val viewPager: ViewPager = view.findViewById(R.id.view_pager)
    viewPager.adapter = sectionsPagerAdapter
    viewPager.offscreenPageLimit = 11;
    setupScrollListener(viewPager, sectionsPagerAdapter)
    val tabs: TabLayout = view.findViewById(R.id.tabs)
    tabs.setupWithViewPager(viewPager)
    return view
  }

  private fun setupScrollListener(viewPager: ViewPager, adapter: SectionsPagerAdapter) {
    viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {

      override fun onPageScrollStateChanged(state: Int) {
        Log.d(TAG, "scrolLStateChange")
      }

      override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
      ) {
        Log.d(TAG, "onPageScrolled")
      }

      override fun onPageSelected(position: Int) {

      }

    })
  }

  override fun onDestroyView() {
    super.onDestroyView()

    val fm = activity?.supportFragmentManager
    val supportMapFragment = fm?.findFragmentById(R.id.mapView) as SupportMapFragment?

    if (supportMapFragment != null) {
      fm?.beginTransaction()?.remove(supportMapFragment)
      fm?.commit{
        commitAllowingStateLoss()
      }
    }
  }

  companion object {
    const val TAG = "StoreTabListActivity"
  }

}
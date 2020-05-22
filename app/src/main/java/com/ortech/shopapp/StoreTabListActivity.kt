package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import android.view.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ortech.shopapp.ui.main.SectionsPagerAdapter

class StoreTabListActivity : Fragment() {

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

  companion object {
    const val TAG = "StoreTabListActivity"
  }

}
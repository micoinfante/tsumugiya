package com.ortech.tsumugiya

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ortech.tsumugiya.Models.Branch
import com.ortech.tsumugiya.ui.main.StoreTabDetailsPagerAdapter
import kotlinx.android.synthetic.main.fragment_store_tab_details.*

private const val ARG_PARAM_BRANCH = "branch"

class StoreTabDetails : Fragment() {

  private var branch: Branch? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      branch = it.getSerializable(ARG_PARAM_BRANCH) as Branch
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupToolBar()
    val sectionPagerAdapter = StoreTabDetailsPagerAdapter(childFragmentManager, branch!!)
    val viewPager = viewPagerStoreDetails
    viewPager.adapter = sectionPagerAdapter
    val tabs = storeTabDetailsTabLayout
    tabs.setupWithViewPager(viewPager)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_store_tab_details, container, false)
  }

  private fun setupToolBar() {
    val toolbar = toolbarStoreTabDetails
    branch?.let {
      toolbar.title = it.branch
    }
    toolbar.setNavigationOnClickListener {
//      activity?.supportFragmentManager?.popBackStack()
      val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
      fragmentTransaction?.remove(this)
      val parentActivity = activity as BottomNavigationActivity
      parentActivity.selectContentFragment(StoreTabListActivity())
//      Log.d(TAG, "Pop from backstack")
    }

  }

  companion object {
    const val TAG = "StoreTabDetails"

    @JvmStatic
    fun newInstance(branch: Branch) =
      StoreTabDetails().apply {
        arguments = Bundle().apply {
          putSerializable(ARG_PARAM_BRANCH, branch)

        }
      }
  }
}
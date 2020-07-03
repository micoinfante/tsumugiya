package com.ortech.tsumugiya.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ortech.tsumugiya.BranchDetails
import com.ortech.tsumugiya.Models.Branch
import com.ortech.tsumugiya.OfferedMenu
import com.ortech.tsumugiya.StoreMapTabFragment

private val TAB_TITLES = arrayOf<String>("店舗説明", "メニュー")

class StoreTabDetailsPagerAdapter(fm: FragmentManager, branch: Branch): FragmentPagerAdapter(fm){

  private var branch: Branch? = null

  init {
    this.branch = branch
  }

  fun setBranch(branch: Branch) {
    this.branch = branch
    notifyDataSetChanged()
  }

  override fun getItem(position: Int): Fragment {
    return if (position == 0) {
      BranchDetails.newInstance(branch!!)
    } else {
      OfferedMenu.newInstance(branch!!)
    }
  }

  override fun getCount(): Int {
    return TAB_TITLES.size
  }

  override fun getPageTitle(position: Int): CharSequence? {
    return TAB_TITLES[position].toString()
  }
}
package com.ortech.tsumugiya.ui.main

import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ortech.tsumugiya.Models.UserSingleton
import com.ortech.tsumugiya.R
import com.ortech.tsumugiya.StoreListFragment
import com.ortech.tsumugiya.StoreMapTabFragment

private val TAB_TITLES = arrayOf<String>("店舗一覧", "地図から探す")

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {

        return if (position == 1) {
            StoreMapTabFragment()
        } else {
            StoreListFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return TAB_TITLES[position].toString()
    }

    override fun getCount(): Int {
        return 2
    }

}
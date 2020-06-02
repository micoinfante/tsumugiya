package com.ortech.shopapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.HeaderListAdapter
import com.ortech.shopapp.Adapters.HomeScreenAdapter
import com.ortech.shopapp.Adapters.StoreListAdapter
import com.ortech.shopapp.Models.UserSingleton
import com.ortech.shopapp.Views.HeaderItemDecoration
import com.ortech.shopapp.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.activity_home_screen.*

class HomeScreen : Fragment() {

  private val mainAdapter = HomeScreenAdapter()
  private var recyclerView: RecyclerView? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    return inflater.inflate(R.layout.activity_home_screen, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    recyclerView = view.findViewById(R.id.homeRecyclerView)

    recyclerView?.apply {
      layoutManager = LinearLayoutManager(this@HomeScreen.context)
      addItemDecoration(HeaderItemDecoration(recyclerView!!, false, isHeader()))
      recyclerView!!.adapter = mainAdapter
    }
    getPoints()

    buttonSettingsDebug.setOnClickListener  {
      val intent = Intent(context, SettingsActivity::class.java)
      startActivity(intent)
    }

    appbarHome.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
      //                    if(verticalOffset == 0 || verticalOffset <= mToolbar.getHeight() && !mToolbar.getTitle().equals(mCollapsedTitle)){
      //                    mCollapsingToolbar.setTitle(mCollapsedTitle);
      //                }else if(!mToolbar.getTitle().equals(mExpandedTitle)){
      //                    mCollapsingToolbar.setTitle(mExpandedTitle);
      //                }
      if (verticalOffset == 0) {
        var layoutParams = buttonSettingsDebug.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = toolbarHomeScreen.height
        buttonSettingsDebug.layoutParams = layoutParams
      } else {
        var layoutParams = buttonSettingsDebug.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = 0
        buttonSettingsDebug.layoutParams = layoutParams
      }
    })

  }

  private fun notifyUpdatedData() {
    (recyclerView?.adapter as HomeScreenAdapter).setPointsData()
  }

  private fun getPoints() {
    // TODO fetch database points and assign to singleton
    val db = Firebase.firestore
    val userID = UserSingleton.instance.userID
    db.collection("PointHistory").whereEqualTo("userID", userID)
      .get()
      .addOnSuccessListener { querySnapshot ->
        val currentPoints = querySnapshot.map { it ->
          it["points"] as Long
        }.sum()
        UserSingleton.instance.setCurrentPoints(currentPoints.toInt())
        notifyUpdatedData()
      }
      .addOnFailureListener {
        UserSingleton.instance.setCurrentPoints(0)
        notifyUpdatedData()
      }

  }

  private fun isHeader() : (itemPosition: Int) -> Boolean {
    return {
      itemPosition ->
      itemPosition == 0
    }
  }

  companion object {
    const val TAG = "HomeScreen"
  }

}
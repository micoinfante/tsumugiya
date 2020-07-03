package com.ortech.tsumugiya

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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.tsumugiya.Adapters.HeaderListAdapter
import com.ortech.tsumugiya.Adapters.HomeScreenAdapter
import com.ortech.tsumugiya.Adapters.StoreListAdapter
import com.ortech.tsumugiya.Models.UserSingleton
import com.ortech.tsumugiya.Models.WebsiteInfo
import com.ortech.tsumugiya.Views.HeaderItemDecoration
import com.ortech.tsumugiya.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.activity_home_screen.*
import java.text.SimpleDateFormat
import java.util.*

class HomeScreen : Fragment() {

  private val mainAdapter = HomeScreenAdapter()
  private var recyclerView: RecyclerView? = null
  private val db = Firebase.firestore
  private var websiteData: WebsiteInfo? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getTotalPointsHistory()
    getWebsiteInfo()
  }

  override fun onResume() {
    super.onResume()
    getTotalPointsHistory()
  }

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

    buttonSettingsDebug.setOnClickListener  {
      val intent = Intent(context, SettingsActivity::class.java)
      startActivity(intent)
    }

    setupAppBar()
  }

  private fun setupAppBar() {
    appbarHome.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
      if (verticalOffset == 0) {
        val layoutParams = buttonSettingsDebug.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = toolbarHomeScreen.height
        buttonSettingsDebug.layoutParams = layoutParams
//        appbarHome.setExpanded(true,true)
      } else {
//        appBarLayout.setExpanded(false, true);
//        appbarHome.setExpanded(false,true)
        val layoutParams = buttonSettingsDebug.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = 0
        buttonSettingsDebug.layoutParams = layoutParams
      }
    })
  }

  private fun notifyUpdatedData() {
    (recyclerView?.adapter as HomeScreenAdapter).setPointsData()
    this.websiteData?.let { (recyclerView?.adapter as HomeScreenAdapter).setWebsiteData(it) }
  }

  private fun getPoints() {

    val userID = UserSingleton.instance.userID
    db.collection("PointHistory").whereEqualTo("userID", userID)
      .whereEqualTo("redeem", "redeemed")
      .get()
      .addOnSuccessListener { querySnapshot ->
        val currentPoints = querySnapshot.map { it ->
          it["points"] as Long
        }.sum()
        UserSingleton.instance.setTotalPoints(currentPoints.toInt())
        notifyUpdatedData()
      }
      .addOnFailureListener {
        UserSingleton.instance.setTotalPoints(0)
        notifyUpdatedData()
      }

  }

  // get pointsToday and lastPoitns transferred
  private fun getTotalPointsHistory() {
    val userID = UserSingleton.instance.userID
    db.collection("TotalPoints").whereEqualTo("userID", userID)
      .addSnapshotListener { querySnapshot , firebaseFirestoreException ->
        // check date
        if (querySnapshot != null) {
          if (querySnapshot.count() != 0) {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val today = sdf.format(Date()).toString()
            val timestamp = querySnapshot.first()["dates"] as Timestamp
            val timestampToday = sdf.format(timestamp.toDate()).toString()
            val currentPoints = querySnapshot.first()["totalPoints"] as Number

            UserSingleton.instance.setCurrentPoints(currentPoints.toInt())
            if (today == timestampToday) {
              Log.d(TAG, "Dates are equal")
              val pointsToday = querySnapshot.first()["pointsToday"] as Number
              val lastPoints = querySnapshot.first()["lastPoints"] as Number
              UserSingleton.instance.setLastPointsTransferred(pointsToday.toInt())
              UserSingleton.instance.setPointsToday(lastPoints.toInt())
            } else {
              UserSingleton.instance.setLastPointsTransferred(0)
              UserSingleton.instance.setPointsToday(0)
            }
            notifyUpdatedData()
          }
        }

        if (firebaseFirestoreException != null) {
          UserSingleton.instance.setLastPointsTransferred(0)
          UserSingleton.instance.setPointsToday(0)
          UserSingleton.instance.setCurrentPoints(0)
          notifyUpdatedData()
        }
      }

  }

  private fun getWebsiteInfo() {
    db.collection("CMSBanner").document("webSiteInfo")
      .get()
      .addOnSuccessListener {
        if (it.exists()) {
          val data = it.data
          val webSub = data?.get("websub") as String
          val webMessage = data["webMessage"] as String
          Log.d(TAG, "$webSub $webMessage")
          val websiteData = it.toObject(WebsiteInfo::class.java)
          this.websiteData = websiteData
          notifyUpdatedData()
        }
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
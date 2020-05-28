package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
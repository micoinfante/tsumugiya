package com.ortech.shopapp

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.PointHistoryAdapter
import com.ortech.shopapp.Models.PointHistory
import com.ortech.shopapp.Models.UserSingleton
import kotlinx.android.synthetic.main.activity_point_history.*

class PointHistoryActivity : AppCompatActivity() {

  private var db = Firebase.firestore
  private var historyList: ArrayList<PointHistory> = ArrayList<PointHistory>()
  private lateinit var historyAdapter: PointHistoryAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_point_history)


    setupToolBar()
    setup()
  }

  private fun setup() {
    historyAdapter = PointHistoryAdapter()
    recyclerViewPointHistory.apply {
      recyclerViewPointHistory.adapter = historyAdapter
      layoutManager = LinearLayoutManager(this.context)
    }
    getPointHistory()
  }

  private fun getPointHistory() {
    db.collection("PointHistory")
      .whereEqualTo("userID", UserSingleton.instance.userID)
      .orderBy("timeStamp", Query.Direction.DESCENDING)
      .get()
      .addOnSuccessListener { querySnapshot ->
        if (querySnapshot.count() == 0) {
          Log.d(TAG, "No Data found")
        }
        querySnapshot.forEach { queryDocumentSnapshot ->
          Log.d(TAG, queryDocumentSnapshot.data.toString())
          val newHistory = queryDocumentSnapshot.toObject(PointHistory::class.java)
          historyList.add(newHistory)
          updateData()
          progressBarHistory.visibility = View.INVISIBLE
        }
      }
      .addOnFailureListener {
        Log.d(TAG, it.localizedMessage)
        Toast.makeText(this.baseContext,it.localizedMessage,Toast.LENGTH_SHORT)
          .show()
      }
  }

  private fun setupToolBar() {
    toolbarHistory.setNavigationOnClickListener {
      finish()
    }
  }

  private fun updateData() {
    historyAdapter.updateData(this.historyList)
  }

  companion object {
    const val TAG = "PointHistoryActivity"
  }


}

package com.ortech.shopapp

import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.AllCouponListAdapter
import com.ortech.shopapp.Models.Coupon
import com.ortech.shopapp.Models.PointHistory
import com.ortech.shopapp.Models.UserSingleton
import com.ortech.shopapp.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.fragment_branch_coupon_list.*

/**
 * A simple [Fragment] subclass.
 */
class BranchCouponList : AppCompatActivity() {
  private lateinit var couponBranchListAdapter: AllCouponListAdapter
  private val db = Firebase.firestore
  private var couponList = ArrayList<Coupon>()
  private var pointHistoryList = ArrayList<PointHistory>()

//  override fun onCreateView(
//    inflater: LayoutInflater, container: ViewGroup?,
//    savedInstanceState: Bundle?
//  ): View? {
//    // Inflate the layout for this fragment
//    return inflater.inflate(R.layout.fragment_branch_coupon_list, container, false)
//  }
//
//  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//    super.onViewCreated(view, savedInstanceState)
//    setup()
//  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.fragment_branch_coupon_list)

    setup()
  }

  private fun setup() {
    couponBranchListAdapter = AllCouponListAdapter()
    branchCouponListRecyclerView.apply {
      layoutManager = LinearLayoutManager(this.context)
      val topSpacing = TopSpacingDecoration(20)
      addItemDecoration(topSpacing)
      branchCouponListRecyclerView.adapter = couponBranchListAdapter
    }

    this.baseContext?.let {
      ContextCompat.getColor(
        it, R.color.primary_orange)
    }?.let { swipeRefreshCouponList.setProgressBackgroundColorSchemeColor(it) }

//    swipeRefreshCouponList.setColorSchemeColors(ContextCompat.getColor(
//      this.context!!, R.color.primary_orange))
    swipeRefreshCouponList.setColorSchemeColors(Color.WHITE)

    swipeRefreshCouponList.setOnRefreshListener {
      fetchCoupons()
      fetchRedeemedCoupons()
    }

    fetchCoupons()
    fetchRedeemedCoupons()
    setupToolBar()
  }

  private fun setupToolBar() {
    val toolbar = toolbarCouponList
    toolbar.setNavigationOnClickListener {
      finish()
    }
  }

  private fun updateData() {
    couponBranchListAdapter.updateData(couponList)
    couponBranchListAdapter.updateTransactionData(pointHistoryList)
  }


  private fun fetchCoupons() {
    couponList.clear()
    updateData()
    db.collection("CMSCoupon").get()
      .addOnSuccessListener {querySnapshot ->
        Log.d(TAG, querySnapshot.size().toString())
        for (queryDocumentSnapshot in querySnapshot) {
          val newCoupon = queryDocumentSnapshot.toObject(Coupon::class.java)
          Log.d(TAG, "new coupon : $newCoupon.id")
          couponList.add(newCoupon)
          updateData()
          progressBarBranchCouponList.visibility = View.GONE
        }
        swipeRefreshCouponList.isRefreshing = false
      }
      .addOnFailureListener {
        Log.e(TAG, "Can't get branch coupons ${it.toString()}")
      }
  }


  private fun fetchRedeemedCoupons() {
    val userID = UserSingleton.instance.userID
    val db = Firebase.firestore.collection("PointHistory")

    db.whereEqualTo("userID", userID)
      .whereEqualTo("redeem", "redeemed")
      .get()
      .addOnSuccessListener {
        it.forEach {pointHistory ->
          val newPointHistory = pointHistory.toObject(PointHistory::class.java)
          pointHistoryList.add(newPointHistory)
          updateData()
        }
      }

  }

  companion object {
    const val TAG = "BranchCouponList"
  }

}


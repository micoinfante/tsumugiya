package com.ortech.shopapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.AllCouponListAdapter
import com.ortech.shopapp.Models.*
import com.ortech.shopapp.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.fragment_branch_coupon_list.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StoreActiveCouponActivity: AppCompatActivity() {
  private lateinit var couponBranchListAdapter: AllCouponListAdapter
  private val db = Firebase.firestore
  private var couponList = ArrayList<Coupon>()
  private var pointHistoryList = ArrayList<PointHistory>()
  private var storeCoupons = HashMap<String, ArrayList<Coupon>>()
  private var formattedCouponData: ArrayList<AdapterItem<Coupon>> = arrayListOf()
  private var currentBranch: Branch? = null

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

    swipeRefreshCouponList.setColorSchemeColors(Color.WHITE)

    swipeRefreshCouponList.setOnRefreshListener {
      formattedCouponData.clear()
      pointHistoryList.clear()
      updateData()
      removeAdapterData()
      branchCouponListRecyclerView.removeAllViewsInLayout()
      fetchRedeemedCoupons()
    }

    fetchCoupons()

    setupToolBar()

  }

  private fun setupToolBar() {
    val toolbar = toolbarCouponList
    toolbar.setNavigationOnClickListener {
      finish()
    }
  }

  private fun removeAdapterData() {
    couponBranchListAdapter.removeAll()
  }

  private fun updateData() {
    couponBranchListAdapter.updateData(formattedCouponData)
    couponBranchListAdapter.updateTransactionData(pointHistoryList)
  }

  private fun fetchCoupons() {
    val branchID = this.currentBranch?.branchID ?: return
    db.collection("CMSCoupon")
      .whereGreaterThanOrEqualTo("untilDate", Timestamp(Date()))
      .whereArrayContains("selectedBranches", branchID)
      .get()
      .addOnSuccessListener {querySnapshot ->
        Log.d(TAG, querySnapshot.size().toString())
        for (queryDocumentSnapshot in querySnapshot) {
          val newCoupon = queryDocumentSnapshot.toObject(Coupon::class.java)
          Log.d(TAG, "new coupon : $newCoupon.id")
          couponList.add(newCoupon)

        }
        fetchRedeemedCoupons()
      }
      .addOnFailureListener {
        Log.e(TAG, "Can't get branch coupons ${it.toString()}")
      }
  }


  private fun fetchRedeemedCoupons() {
    val userID = UserSingleton.instance.userID
    val db = Firebase.firestore.collection("PointHistory")

    progressBarBranchCouponList.visibility = View.VISIBLE
    db.whereEqualTo("userID", userID)
      .whereEqualTo("redeem", "redeemed")
      .get()
      .addOnSuccessListener { it ->
        Log.d(TAG, "Point History Count: ${it.count()} ")
        it.forEach {pointHistory ->
          val newPointHistory = pointHistory.toObject(PointHistory::class.java)
          pointHistoryList.add(newPointHistory)

        }
        updateStoreListHeaders()
        Log.d(TAG, "Point History Count: ${pointHistoryList.map { ph -> ph.couponID }} ")
        progressBarBranchCouponList.visibility = View.GONE
        swipeRefreshCouponList.isRefreshing = false
      }
      .addOnFailureListener {
        Log.w(TAG, it.localizedMessage)
      }
  }

  private fun updateStoreListHeaders() {
    formattedCouponData.clear()
    this.couponList.map { it ->
      it.selectedStoreName?.forEach { storeName ->
        val currentCoupons = this.storeCoupons[storeName]
        if (currentCoupons != null) {
          // if hashmap value of currentCoupons doesn't have the coupon being parse, add it
          // else just continue
          // to avoid duplicate items
          if (!currentCoupons.map { it -> it.couponID }.contains(it.couponID)) {
            currentCoupons.add(it)
          }
        } else {
          this.storeCoupons[storeName] = arrayListOf(it)
        }
      }
    } // couponList

    for ((header, coupons) in storeCoupons) {
      val newCouponHeader = Coupon(header)
      val newAdapterItem = AdapterItem(newCouponHeader, AllCouponListAdapter.TYPE_HEADER)
      formattedCouponData.add(newAdapterItem)
      Log.d(TAG, "FormatCouponStore[${header}] [${coupons.count()}] \n${coupons.map{it.couponID}}")

      coupons.forEach {coupon ->
        val newCoupon = Coupon(header, coupon.storeID, coupon.couponDetails, coupon.couponID, coupon.couponLabel, coupon.fromDate, coupon.imageURL, coupon.isEnabled, coupon.mainID, coupon.orderBy, coupon.points, coupon.selectedBranches, coupon.selectedStoreName, coupon.untilDate, coupon.fromDateStr, coupon.untilDateStr)
        if (header == "二代目らーめん世界") {
          Log.d(TAG,"setting header: $header $newCoupon")
        }
        newCoupon.couponStore = header
        formattedCouponData.add(AdapterItem(newCoupon, AllCouponListAdapter.TYPE_COUPON, false))
      }

      updateData()

    }
  }

  private fun incrementDay(date: Date): Date {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.time = date
    cal.add(Calendar.DATE, 1)
    return cal.time
  }


  companion object {
    const val TAG = "StoreActiveCopon"
  }

}
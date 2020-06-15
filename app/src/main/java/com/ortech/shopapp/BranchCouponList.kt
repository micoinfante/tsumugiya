package com.ortech.shopapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.AllCouponListAdapter
import com.ortech.shopapp.Models.AdapterItem
import com.ortech.shopapp.Models.Coupon
import com.ortech.shopapp.Models.PointHistory
import com.ortech.shopapp.Models.UserSingleton
import com.ortech.shopapp.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.fragment_branch_coupon_list.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class BranchCouponList : AppCompatActivity() {
  private lateinit var couponBranchListAdapter: AllCouponListAdapter
  private val db = Firebase.firestore
  private var couponList = ArrayList<Coupon>()
  private var pointHistoryList = ArrayList<PointHistory>()
  private var storeCoupons = HashMap<String, ArrayList<Coupon>>()
  private var formattedCouponData: ArrayList<AdapterItem<Coupon>> = arrayListOf()

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
//      fetchCoupons()
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

    db.collection("CMSCoupon").get()
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
//    "E362AFB5-51F4-474E-99D5-BD2E4A71A606"
    db.whereEqualTo("userID", userID)
      .whereEqualTo("redeem", "redeemed")
      .get()
      .addOnSuccessListener { it ->
        Log.d(TAG, "Point History Count: ${it.count()} ")
        it.forEach {pointHistory ->
          val newPointHistory = pointHistory.toObject(PointHistory::class.java)
          pointHistoryList.add(newPointHistory)
          updateStoreListHeaders()
//          updateData()
//          updateData()
        }
        Log.d(TAG, "Point History Count: ${pointHistoryList.map { ph -> ph.couponID }} ")
        progressBarBranchCouponList.visibility = View.GONE
        swipeRefreshCouponList.isRefreshing = false

//        fetchCoupons()
      }
      .addOnFailureListener {
//        fetchCoupons()
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

    storeCoupons.forEach { storeCoupon ->

      val coupons = storeCoupon.value.map { it -> it.couponID }.toString().split(",")
      Log.d(TAG, "CouponStore[${storeCoupon.key}][${storeCoupon.value.count()}] \n${coupons}")
    }

    Log.d(TAG, "Total Stores Headers: ${storeCoupons.keys.count()} ${storeCoupons.keys}")

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

//      coupons.forEach {coupon ->
//        pointHistoryList.forEach { redeemedCoupon ->
//
//          if (redeemedCoupon.couponID == coupon.couponID && redeemedCoupon.branchName == newCouponHeader.couponStore) {
//            Log.d(TAG, "${redeemedCoupon.couponID} == ${coupon.couponID}")
//            val validDate = redeemedCoupon.timeStamp?.toDate()
//            if (Date() >= validDate) {
//              formattedCouponData.add(AdapterItem(coupon, AllCouponListAdapter.TYPE_COUPON,true))
//            } else {
//              formattedCouponData.add(AdapterItem(coupon, AllCouponListAdapter.TYPE_COUPON, false))
//            }
//          } else {
//            formattedCouponData.add(AdapterItem(coupon, AllCouponListAdapter.TYPE_COUPON, false))
//          }
//
//        }
//      }

      updateData()

    }

    Log.d(TAG, "Formatted Data count: ${formattedCouponData.count()} $formattedCouponData")
//    updateData()
  }

  private fun incrementDay(date: Date): Date {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.time = date
    cal.add(Calendar.DATE, 1)
    return cal.time
  }


  companion object {
    const val TAG = "BranchCouponList"
  }

}


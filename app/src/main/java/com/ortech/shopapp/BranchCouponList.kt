package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.AllCouponListAdapter
import com.ortech.shopapp.Models.Coupon
import com.ortech.shopapp.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.fragment_branch_coupon_list.*

/**
 * A simple [Fragment] subclass.
 */
class BranchCouponList : Fragment() {
  private lateinit var couponBranchListAdapter: AllCouponListAdapter
  private val db = Firebase.firestore
  private var couponList = ArrayList<Coupon>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_branch_coupon_list, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setup()
  }

  private fun setup() {
    couponBranchListAdapter = AllCouponListAdapter()
    branchCouponListRecyclerView.apply {
      layoutManager = LinearLayoutManager(this.context)
      val topSpacing = TopSpacingDecoration(5)
      addItemDecoration(topSpacing)
      branchCouponListRecyclerView.adapter = couponBranchListAdapter
    }
    fetchCoupons()
    setupToolBar()
  }

  private fun setupToolBar() {
    val toolbar = toolbarCouponList
    toolbar.setNavigationOnClickListener {
      activity?.supportFragmentManager?.popBackStack()
    }
//    val currentActivity = (activity as AppCompatActivity)
//    currentActivity.setSupportActionBar(toolbar)
//    currentActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
//    currentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }


  private fun fetchCoupons() {
    db.collection("CMSCoupon").get()
      .addOnSuccessListener {querySnapshot ->
        Log.d(TAG, querySnapshot.size().toString())
        for (queryDocumentSnapshot in querySnapshot) {
          val newCoupon = queryDocumentSnapshot.toObject(Coupon::class.java)
          Log.d(TAG, "new coupon : $newCoupon.id")
          couponList.add(newCoupon)
          couponBranchListAdapter.updateData(couponList)
        }
      }
      .addOnFailureListener {
        Log.e(TAG, "Can't get branch coupons ${it.toString()}")
      }
  }

  companion object {
    const val TAG = "BranchCouponList"
  }

}


package com.ortech.shopapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ortech.shopapp.Adapters.AllCouponListAdapter
import com.ortech.shopapp.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.fragment_branch_coupon_list.*

/**
 * A simple [Fragment] subclass.
 */
class BranchCouponList : Fragment() {
  private lateinit var couponBranchListAdapter: AllCouponListAdapter

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
      val topSpacing = TopSpacingDecoration(0)
      addItemDecoration(topSpacing)
      branchCouponListRecyclerView.adapter = couponBranchListAdapter
    }
//    getBranches()

  }

}


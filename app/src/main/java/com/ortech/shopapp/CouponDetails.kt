package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_branch_coupon_item.*
import kotlinx.android.synthetic.main.fragment_coupon_details.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_COUPON_NAME = "couponName"
private const val ARG_COUPON_TIMESTAMP = "couponTimestamp"
private const val ARG_COUPON_POINTS = "couponPoints"

/**
 * A simple [Fragment] subclass.
 * Use the [CouponDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class CouponDetails : Fragment() {
  // TODO: Rename and change types of parameters
  private var couponName: String? = null
  private var couponTimestamp: String? = null
  private var couponPoint: Int? = null

//  private var textViewCouponName = textViewCouponDetailsName
//  private var textViewTimestamp = textViewCouponDetailsTimestamp
//  private var textViewPoints = textViewCouponDetailsPoints

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      couponName = it.getString(ARG_COUPON_NAME)
      couponTimestamp = it.getString(ARG_COUPON_TIMESTAMP)
      couponPoint = it.getInt(ARG_COUPON_POINTS)

    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_coupon_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    textViewCouponDetailsName.text = couponName
    textViewCouponDetailsTimestamp.text = couponTimestamp
    textViewCouponDetailsPoints.text = couponPoint.toString()

  }

  companion object {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CouponDetails.
     */
    // TODO: Rename and change types and number of parameters
    @JvmStatic
    fun newInstance(param1: String, param2: String) =
      CouponDetails().apply {
        arguments = Bundle().apply {
          putString(ARG_COUPON_NAME, couponName)
          putString(ARG_COUPON_TIMESTAMP, couponTimestamp)
          couponPoint?.let { putInt(ARG_COUPON_POINTS, it) }
        }
      }
  }
}

package com.ortech.shopapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ortech.shopapp.Models.Coupon
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_branch_coupon_item.*
import kotlinx.android.synthetic.main.fragment_coupon_details.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_COUPON = "coupon"

/**
 * A simple [Fragment] subclass.
 * Use the [CouponDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class CouponDetails : Fragment() {
  // TODO: Rename and change types of parameters
  private var coupon: Coupon? = null

//  private var textViewCouponName = textViewCouponDetailsName
//  private var textViewTimestamp = textViewCouponDetailsTimestamp
//  private var textViewPoints = textViewCouponDetailsPoints

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      coupon = it.getSerializable(ARG_COUPON) as? Coupon
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

    textViewCouponDetailsName.text = coupon?.couponLabel ?: ""
    textViewCouponDetailsTimestamp.text = coupon?.untilDate?.toDate().toString()  ?: Date().toString()
    textViewCouponDetailsPoints.text = getString(R.string.text_label_point, coupon?.points.toString())

    Picasso.get().load(Uri.parse(coupon?.imageURL))
      .into(imageViewCouponDetail)

  }

  companion object {
    @JvmStatic
    fun newInstance(coupon: Coupon) =
      CouponDetails().apply {
        arguments = Bundle().apply {
          putSerializable(ARG_COUPON, coupon)
        }
      }
  }
}

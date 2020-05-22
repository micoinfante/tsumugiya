package com.ortech.shopapp

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.ortech.shopapp.Models.Coupon
import com.ortech.shopapp.Models.UserSingleton
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_branch_coupon_item.*
import kotlinx.android.synthetic.main.fragment_branch_coupon_list.*
import kotlinx.android.synthetic.main.fragment_coupon_details.*
import java.util.*

private const val ARG_COUPON = "coupon"

class CouponDetails : Fragment() {


  private var coupon: Coupon? = null

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

    return inflater.inflate(R.layout.fragment_coupon_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupToolBar()

    val date = coupon?.untilDate?.toDate().toString()
    textViewCouponDetailsName.text = coupon?.couponLabel ?: ""
    textViewCouponDetailsTimestamp.text = date.substring(0, date.indexOf("GMT"))
    textViewCouponDetailsPoints.text = getString(R.string.text_label_point, coupon?.points.toString())
    textViewCouponDetailsCurrentPoints.text = UserSingleton.instance.getCurrentPoints().toString()
    generateQRCode()
    Glide.with(view)
      .load(Uri.parse(coupon?.imageURL))
      .apply(RequestOptions.circleCropTransform())
      .into(object: CustomViewTarget<ImageView, Drawable>(imageViewCouponDetail){
        override fun onLoadFailed(errorDrawable: Drawable?) {
        }

        override fun onResourceCleared(placeholder: Drawable?) {

        }

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
          imageViewCouponDetail.setImageDrawable(resource)
        }

      })

  }

  private fun setupToolBar() {
    val toolbar = toolbarCouponDetail
    toolbar.setNavigationOnClickListener {
      activity?.supportFragmentManager?.popBackStack()
    }
  }

  private fun generateQRCode() {
    val imageView = view?.findViewById<ImageView>(R.id.imageViewCouponDetailQRCode)
    val userID = UserSingleton.instance.userID
    val content = "$userID, ${coupon?.couponId}, ${coupon?.points}, ${coupon?.couponLabel}"

    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 1000, 1000)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
      for (y in 0 until height) {
        bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
      }
    }
    imageView?.setImageBitmap(bitmap)
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

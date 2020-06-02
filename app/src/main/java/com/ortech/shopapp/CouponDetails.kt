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
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
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
    val imageView = imageViewCouponDetailQRCode
    val userID = UserSingleton.instance.userID
    val content = "$userID, ${coupon?.couponID}, ${coupon?.points}, ${coupon?.couponLabel}"

    val multiFormatWriter = MultiFormatWriter()
    val hintMap = mapOf(EncodeHintType.MARGIN to 0)
    val bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512, hintMap)
    val qrCode = BarcodeEncoder().createBitmap(bitMatrix)

    Glide.with(this.view!!).load(qrCode).into(imageView)
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

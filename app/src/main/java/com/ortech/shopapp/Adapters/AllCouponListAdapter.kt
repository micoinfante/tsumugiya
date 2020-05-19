package com.ortech.shopapp.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.common.io.Resources.getResource
import com.ortech.shopapp.BranchCouponList
import com.ortech.shopapp.CouponDetails
import com.ortech.shopapp.Models.Coupon
import com.ortech.shopapp.R
import com.squareup.picasso.Picasso
import io.grpc.internal.SharedResourceHolder
import kotlinx.android.synthetic.main.fragment_branch_coupon_item.view.*
import kotlinx.android.synthetic.main.fragment_home_fifth_section.view.*
import kotlinx.android.synthetic.main.fragment_home_fourth_section.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_item.view.*

class AllCouponListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
  private var couponList = ArrayList<Coupon>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return CouponViewHolder(inflater.inflate(R.layout.fragment_branch_coupon_item, parent, false))
  }

  override fun getItemViewType(position: Int): Int {
    return position
  }

  override fun getItemCount(): Int {
    return couponList.size
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is CouponViewHolder) {
      holder.bind(couponList[position])
    }
  }

  fun updateData(couponList: ArrayList<Coupon>) {
    this.couponList = couponList
    notifyDataSetChanged()
  }

  inner class CouponViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val couponName = itemView.textViewCouponItemName
    private val timestamp = itemView.textViewCouponItemTimestamp
    private val requiredPoints = itemView.textViewCouponItemPoints
    private val couponThumbnail = itemView.imageViewCouponItemThumbnail

    fun bind(coupon: Coupon) {
      couponName.text = coupon.couponLabel
      timestamp.text = coupon.untilDate?.toDate().toString()
      val pointText = itemView.context.getString(R.string.text_label_point, coupon.points)
      requiredPoints.text = pointText

//      Picasso.get().load()
//        .fit()
//        .centerCrop()
//        .into(couponThumbnail)
      Glide.with(itemView)
        .load(Uri.parse(coupon.imageURL))
        .into(couponThumbnail)


      itemView.setOnClickListener {
        val activity = itemView.context as AppCompatActivity
        val fragment = CouponDetails.newInstance(coupon)
        val transaction =  activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
      }

    }

  }

}



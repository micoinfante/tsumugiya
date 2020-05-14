package com.ortech.shopapp.Adapters

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.common.io.Resources.getResource
import com.ortech.shopapp.Models.Coupon
import com.ortech.shopapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home_fifth_section.view.*
import kotlinx.android.synthetic.main.fragment_home_fourth_section.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_item.view.*

class AllCouponListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return CouponViewHolder(inflater.inflate(R.layout.fragment_branch_coupon_item, parent, false))
  }


  override fun getItemViewType(position: Int): Int {
    return position
  }

  override fun getItemCount(): Int {
    return 6
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is CouponViewHolder) {

    }
  }

  inner class CouponViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

  }

}



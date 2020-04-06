package com.ortech.shopapp.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ortech.shopapp.Models.Store
import com.ortech.shopapp.R
import com.ortech.shopapp.StoreItemFragment
import com.ortech.shopapp.StoreListFragment
import kotlinx.android.synthetic.main.fragment_store_item.view.*

class StoreListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return StoreItemViewHolder(
    inflater.inflate(R.layout.fragment_store_item, parent, false)
    )
  }

  override fun getItemViewType(position: Int): Int {
    return when(position) {
      0 -> 0
      1 -> 1
      else -> position
    }
  }

  override fun getItemCount(): Int {
    Log.d("HomeScreen", "Number Of Sections: 5")
    return 5
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

  }


  class StoreItemViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val storeName = itemView.tvStoreName
    private val storeAddress = itemView.tvAddress
    private val storePhone = itemView.tvPhoneNumber
    private val storeHours = itemView.tvTime
    private val storePicture = itemView.imageViewStore

    fun bind(store: Store) {
      storeName.text = store.name
      storeAddress.text = store.address
      storePhone.text = store.phone
      storeHours.text = store.hours
    }
  }
}



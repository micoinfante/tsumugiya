package com.ortech.shopapp.Adapters

import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.ortech.shopapp.Models.Store
import com.ortech.shopapp.R
import com.ortech.shopapp.StoreItemFragment
import com.ortech.shopapp.StoreListFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_store_item.view.*

class StoreListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
  private var stores = arrayListOf<Store>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return StoreItemViewHolder(
    inflater.inflate(R.layout.fragment_store_item, parent, false)
    )
  }

  override fun getItemViewType(position: Int): Int {
    return position
  }

  override fun getItemCount(): Int {
    return stores.size
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when(holder) {
      is StoreItemViewHolder -> {
        holder.apply {
          Log.d(TAG, "New Store: $position")
          val store = stores[position]
          holder.bind(store)
        }
      }
    }
  }

  fun updateData(stores: ArrayList<Store>) {
    this.stores = stores
    notifyDataSetChanged()
  }


  class StoreItemViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val storeName = itemView.tvStoreName
    private val storeAddress = itemView.tvAddress
    private val storePhone = itemView.tvPhoneNumber
    private val storeHours = itemView.tvTime
    private val storePicture = itemView.imageViewStore

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bind(store: Store) {
      storeName.text = store.branch
      storeAddress.text = store.location
      storePhone.text = store.phone
      @SuppressWarnings
      storeHours.text = "${store.opening}: ${store.closing}"
      storePicture.clipToOutline = true
      Picasso.get()
        .load(Uri.parse(store.branchURLImages))
        .into(storePicture)

    }
  }

  companion object {
    const val TAG = "StoreListAdapter"
  }
}



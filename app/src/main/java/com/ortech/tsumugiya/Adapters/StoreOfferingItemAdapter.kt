package com.ortech.tsumugiya.Adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ortech.tsumugiya.Models.Branch
import com.ortech.tsumugiya.R
import kotlinx.android.synthetic.main.fragment_store_offering_item.view.*

class StoreOfferingItemAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private var selectedBranchList: ArrayList<Branch> = arrayListOf()


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return StoreOfferingItemViewHolder(LayoutInflater
      .from(parent.context)
      .inflate(R.layout.fragment_store_offering_item, parent, false)
    )
  }

  override fun getItemCount(): Int {
    return selectedBranchList.size
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is StoreOfferingItemAdapter.StoreOfferingItemViewHolder) {
      holder.bind(selectedBranchList[position])
    }
  }

  fun updateDataSet(selectedBranchList: ArrayList<Branch>) {
    this.selectedBranchList = selectedBranchList
    notifyDataSetChanged()
  }

  inner class StoreOfferingItemViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val title = itemView.textViewStoreOfferingTitle
    private val subtitle = itemView.textViewStoreOfferingItemDetails
    private val button = itemView.imageViewStoreOfferingItemInfo


    fun bind(branch: Branch) {
      title.text = branch.branch
      subtitle.text = branch.location

      button.setOnClickListener {
        var uri: Uri?

        if (branch.latitude == 0.toDouble() || branch.longitude == 0.toDouble()) {
          uri = Uri.parse("https://www.google.com.ph/maps/search/${branch.location}")
          val intent = Intent(Intent.ACTION_VIEW, uri)
          intent.setPackage("com.google.android.apps.maps")
          itemView.context.startActivity(intent)
        } else {
          val geoUri = Uri.parse("geo:${branch.latitude},${branch.longitude}")
          var intent = Intent(Intent.ACTION_VIEW, geoUri)
          intent.setPackage("com.google.android.apps.maps")
          if (intent.resolveActivity(itemView.context.packageManager) !== null) {
            itemView.context.startActivity(intent)
          } else {
            uri = Uri.parse("http://maps.google.com/maps?saddr=${branch.latitude},${branch.longitude}")
            intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            itemView.context.startActivity(intent)
          }
        }
      }
    }

  }

}
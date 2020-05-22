package com.ortech.shopapp.Adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.ortech.shopapp.Models.Branch
import com.ortech.shopapp.R
import kotlinx.android.synthetic.main.fragment_home_screen_header.view.*

class BranchDetailsAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private var itemCount = 2

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return if (viewType==0) {
        BranchHeaderHolder(inflater.inflate(R.layout.fragment_home_screen_header, parent, false))
    } else BranchDetailsHolder(inflater.inflate(R.layout.branch_details_section, parent, false))
  }

  override fun getItemViewType(position: Int): Int {
    return if(position==0) {
      TYPE_HEADER
    } else TYPE_DETAIL
  }

  override fun getItemCount(): Int {
    return itemCount
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when(holder) {
      is BranchHeaderHolder -> holder.bind()
      is BranchDetailsHolder -> holder.bind()
    }
  }

  inner class BranchHeaderHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.imageViewHeader

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bind() {
      imageView.setImageDrawable(itemView.context.getDrawable(R.drawable.app_logo_sekai))
    }

  }

  inner class BranchDetailsHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind() {

    }
  }

  companion object {
    const val TYPE_HEADER = 0
    const val TYPE_DETAIL = 1
  }

}
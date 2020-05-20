package com.ortech.shopapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ortech.shopapp.R
import kotlinx.android.synthetic.main.fragment_sticky_header.view.*
import okhttp3.internal.http2.Header
import java.util.*
import java.util.zip.Inflater

class HeaderListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private val headerCount: Int = 1

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return HeaderItemViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.fragment_sticky_header, parent, false)
    )
  }

  override fun getItemCount(): Int {
    return headerCount
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is HeaderItemViewHolder -> (holder as HeaderItemViewHolder)
    }
  }

  class HeaderItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

  }

  companion object TYPE {
    const val SECTION = 0
    const val ITEM = 1
  }
}
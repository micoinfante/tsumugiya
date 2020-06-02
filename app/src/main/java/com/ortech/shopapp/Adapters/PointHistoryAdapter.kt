package com.ortech.shopapp.Adapters

import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ortech.shopapp.Models.PointHistory
import com.ortech.shopapp.R
import kotlinx.android.synthetic.main.point_history_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PointHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
  private var historyList = arrayListOf<PointHistory>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return HistoryItemViewHolder(
      inflater.inflate(R.layout.point_history_item, parent, false)
    )
  }

  override fun getItemViewType(position: Int): Int {
    return position
  }

  override fun getItemCount(): Int {
    return historyList.size
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when(holder) {
      is HistoryItemViewHolder -> {
        holder.apply {
          val history = historyList[position]
          holder.bind(history)
        }
      }
    }
  }

  fun updateData(historyList: ArrayList<PointHistory>) {
    this.historyList = historyList
    notifyDataSetChanged()
  }

  inner class HistoryItemViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val thumbnail = itemView.imageViewHistoryThumbnail
    private val timestamp = itemView.textViewHistoryDate
    private val details = itemView.textViewHistoryDetails
    private val points = itemView.textViewHistoryPoint
    private val res = itemView.context.resources

    fun bind(history: PointHistory) {
      val date = history.timeStamp?.toDate()
      val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
      timestamp.text = sdf.format(date!!).toString()
      details.text = history.branchName
      points.text = history.points.toString()

      Glide.with(itemView)
        .load(Uri.parse(history.branchURL))
        .circleCrop()
        .into(thumbnail)

    }
  }

  companion object {
    const val TAG = "StoreListAdapter"
  }
}

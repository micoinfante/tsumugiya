package com.ortech.tsumugiya.Adapters

import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ortech.tsumugiya.Models.PointHistory
import com.ortech.tsumugiya.R
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
      points.text = res.getString(R.string.text_label_point, history.points.toString())

      if (history.transfer == "transferred") {
        points.setTextColor(ContextCompat.getColor(itemView.context,R.color.primary_green))
      } else {
        points.setTextColor(ContextCompat.getColor(itemView.context,R.color.primary_red))
      }

      Glide.with(itemView)
        .load(Uri.parse(history.branchURL))
        .into(thumbnail)

    }
  }

  companion object {
    const val TAG = "StoreListAdapter"
  }
}

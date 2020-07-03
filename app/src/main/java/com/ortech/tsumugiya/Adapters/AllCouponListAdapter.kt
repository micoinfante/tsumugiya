package com.ortech.tsumugiya.Adapters


import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ortech.tsumugiya.CountDownAlert
import com.ortech.tsumugiya.CouponDetails
import com.ortech.tsumugiya.Models.AdapterItem
import com.ortech.tsumugiya.Models.CMSSettings
import com.ortech.tsumugiya.Models.Coupon
import com.ortech.tsumugiya.Models.PointHistory
import com.ortech.tsumugiya.R
import kotlinx.android.synthetic.main.fragment_branch_coupon_item.view.*
import kotlinx.android.synthetic.main.fragment_sticky_header.view.*
import kotlinx.android.synthetic.main.header_coupon.view.*

import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.time.milliseconds

class AllCouponListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

  private var couponList = ArrayList<Coupon>()
  private var pointHistoryList = ArrayList<PointHistory>()
  private var storeCouponList: ArrayList<AdapterItem<Coupon>> = arrayListOf()

  init {
    setHasStableIds(true)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return if (viewType == TYPE_HEADER) {
      CouponHeaderHolder(inflater.inflate(R.layout.header_coupon, parent,false))
    } else {
      CouponViewHolder(inflater.inflate(R.layout.fragment_branch_coupon_item, parent, false))
    }
  }


  override fun getItemViewType(position: Int) : Int {
    return if (storeCouponList[position].viewType == TYPE_HEADER) {
        TYPE_HEADER
    } else {
        TYPE_COUPON
    }
  }

  override fun getItemCount(): Int {
    Log.d(TAG, "Number of items: ${storeCouponList.count()}")
    return storeCouponList.count()
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    holder.setIsRecyclable(false)
    when(holder) {
      is CouponHeaderHolder -> {
        val title = storeCouponList[position].value?.couponStore
        if (title != null) {
          holder.bind(title)
        } else {
          holder.bind("error")
        }
      }
      is AllCouponListAdapter.CouponViewHolder -> {
        if (storeCouponList[position].viewType == TYPE_COUPON) {
          storeCouponList[position].let { holder.bind(it) }
        }
      }
    }

  }

  fun updateData(storeCouponList: ArrayList<AdapterItem<Coupon>>) {
    Log.d(TAG, "Updated Number of items: ${storeCouponList.count()}")
    this.storeCouponList = storeCouponList
    notifyDataSetChanged()
  }

  fun removeAll() {
    this.storeCouponList.clear()
    this.pointHistoryList.clear()
    notifyDataSetChanged()
    Log.d(TAG, "Deleted all data: ${storeCouponList.count()}")
  }

  fun updateTransactionData(pointHistoryList: ArrayList<PointHistory>) {
    this.pointHistoryList = pointHistoryList
    notifyDataSetChanged()
  }


  inner class CouponHeaderHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val header = itemView.textViewStoreHeader

    fun bind(title: String) {
      header.text = title
    }
  }

  inner class CouponViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val couponName = itemView.textViewCouponItemName
    private val timestamp = itemView.textViewCouponItemTimestamp
    private val requiredPoints = itemView.textViewCouponItemPoints
    private val couponThumbnail = itemView.imageViewCouponItemThumbnail
    private val couponDetails = itemView.textVIewCouponDetails
    private val timerShade = itemView.cardViewTimer
    private val timer = itemView.textViewCouponTimer
    private val couponItem = itemView.cardViewCouponItem
    private val res = itemView.context

    fun bind(item: AdapterItem<Coupon>) {
      val coupon = item.value?:return
      val isCouponUsed = checkRedemption(item)

      val date = coupon.untilDate?.toDate().toString()
      couponName.text = coupon.couponLabel
      timestamp.text = date.substring(0, date.indexOf("GMT"))
      val pointText = itemView.context.getString(R.string.text_label_point, coupon.points)
      requiredPoints.text = pointText
      couponDetails.text = coupon.couponDetails

      Glide.with(itemView)
        .load(Uri.parse(coupon.imageURL))
        .centerCrop()
        .into(couponThumbnail)

    }

    private fun checkRedemption(item: AdapterItem<Coupon>): Boolean {
      val coupon = item.value!!
      if (pointHistoryList.count() == 0) {
        itemView.setOnClickListener {
          val intent = Intent(itemView.context, CouponDetails::class.java)
          intent.putExtra(CouponDetails.ARG_COUPON, coupon as Parcelable)
          itemView.context.startActivity(intent)
        }
      }
      Log.d(TAG, "PointHistory ${pointHistoryList.map { it -> it.branchName
      }} \nCurrentCoupon: ${coupon.couponStore} $coupon")
      pointHistoryList.forEach {pointHistory ->
        Log.d(TAG, "Cell  ${coupon.couponStore} == ${pointHistory.branchName}")
        if (pointHistory.couponID == coupon.couponID && coupon.couponStore == pointHistory.branchName) {
            val today = Date()
            val pointHistoryRedemption = pointHistory.timeStamp?.toDate()

            val couponAvailability = pointHistoryRedemption?.let { incrementDay(it) }

            if (today > couponAvailability) {
              timerShade.visibility = View.INVISIBLE
              itemView.setOnClickListener {
                val intent = Intent(itemView.context, CouponDetails::class.java)
                intent.putExtra(CouponDetails.ARG_COUPON, coupon as Parcelable)
                itemView.context.startActivity(intent)
              }
            } else {
              val diff = (couponAvailability?.time ?: Date().time) - Date().time
              val remainingTime = Date(diff)
              addCountDownTimer(remainingTime)
              timerShade.visibility = View.VISIBLE
              timerShade.setOnClickListener {
                val currentMillis = (couponAvailability?.time ?: Date().time) - Date().time
                val activity = itemView.context as AppCompatActivity
                val fragment = CountDownAlert.newInstance(currentMillis)
                val transaction =  activity.supportFragmentManager.beginTransaction()
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(R.id.branchCouponContainer, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
              }
              return true
            }
        } else {
          itemView.setOnClickListener {
              val intent = Intent(itemView.context, CouponDetails::class.java)
              intent.putExtra(CouponDetails.ARG_COUPON, coupon as Parcelable)
              itemView.context.startActivity(intent)
            }
          }
      }
      return false
    }

    private fun addCountDownTimer(date: Date) {

      val cal = Calendar.getInstance()
      cal.time = date
      val millis = cal.timeInMillis

      object : CountDownTimer(millis, 1000) {
        override fun onFinish() {
         timerShade.visibility = View.INVISIBLE
        }

        override fun onTick(millisUntilFinished: Long) {

          timer.text = String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
          )
        }
      }.start()
    }

    private fun incrementDay(date: Date): Date {
      val cal = Calendar.getInstance(Locale.getDefault())
      cal.time = date
      cal.add(Calendar.SECOND, CMSSettings.instance.currentCouponTimeLimit().toInt())
      return cal.time
    }

  }

  companion object {
    const val TAG = "CouponListAdapter"
    const val TYPE_HEADER = 0
    const val TYPE_COUPON = 1
  }

}



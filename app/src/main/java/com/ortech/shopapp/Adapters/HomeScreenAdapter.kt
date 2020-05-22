package com.ortech.shopapp.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.common.io.Resources.getResource
import com.ortech.shopapp.BranchCouponList
import com.ortech.shopapp.Models.UserSingleton
import com.ortech.shopapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home_fifth_section.view.*
import kotlinx.android.synthetic.main.fragment_home_fourth_section.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_header.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_item.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_points.view.*
import kotlinx.android.synthetic.main.fragment_sticky_header.view.*
import java.util.*
import kotlin.collections.HashMap

class HomeScreenAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

  private val itemCount = 7
  private val points = Pair("current" to 0, "total" to 0)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)


    return when(viewType) {
      0 -> {
        StickyHeaderSection(
          inflater.inflate(R.layout.fragment_sticky_header, parent, false)
        )
      }
      1 -> {
        HomeScreenHeader(
          inflater.inflate(R.layout.fragment_home_screen_header, parent, false)
        )
      }
      2 -> {
        HomeScreenPoints(
          inflater.inflate(R.layout.fragment_home_screen_points, parent, false)
        )
      }
      4 -> {
        HomeScreenFourthSection(inflater.inflate(R.layout.fragment_home_fourth_section, parent, false))
      }
      5  -> {
        HomeScreenFifthSection(inflater.inflate(R.layout.fragment_home_fifth_section, parent, false))
      }
      else -> {
        HomeScreenItem(
          inflater.inflate(R.layout.fragment_home_screen_item, parent, false)
        )
      }
    }

  }

  fun setPointsData() {
    notifyItemChanged(2)
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when(position) {
      0 -> (holder as StickyHeaderSection).bind()
      1 -> (holder as HomeScreenHeader).bind()
      2 -> (holder as HomeScreenPoints).bind()
      3 -> (holder as HomeScreenItem).bind(TYPE.NOTICE)
      4 -> (holder as HomeScreenFourthSection).bind()
      5 -> (holder as HomeScreenFifthSection).bind()
      6 -> (holder as HomeScreenItem).bind(TYPE.POINT_HISTORY)
    }
  }

  override fun getItemViewType(position: Int): Int {
    return position
  }

  override fun getItemCount(): Int {
    return itemCount
  }

  class HomeScreenItem constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val buttonCouponItem = itemView.buttonCouponItem
    private val homeItemTitle = itemView.textViewHomeItemTitle
    private val homeItemSubtitle = itemView.textViewHomeItemSubTitle
    private val homeItemThumbnail = itemView.imageViewHomeTitle
    private val res = itemView.context

    fun bind(type: TYPE) {
      when (type) {
        TYPE.NOTICE -> {
          homeItemTitle.text = res.getString(R.string.homescreen_notice_title)
          homeItemSubtitle.text = res.getString(R.string.homescreen_notice_subtitle)
          buttonCouponItem.text = res.getString(R.string.homescreen_notice_button_title)
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            homeItemThumbnail.setImageDrawable(res.getDrawable(R.drawable.point_history))
          }
          buttonCouponItem.setOnClickListener {
            val activity = itemView.context as AppCompatActivity
            val fragment = BranchCouponList()
            val transaction =  activity.supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
              R.anim.enter_from_right,
              R.anim.exit_to_left,
              R.anim.enter_from_left,
              R.anim.exit_to_right
            )
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack("BranchCouponList")
            transaction.commit()
          }
        }
        TYPE.POINT_HISTORY -> {
          homeItemTitle.text = res.getString(R.string.homescreen_point_history_title)
          homeItemSubtitle.text = res.getString(R.string.homescreen_point_history_subtitle)
          buttonCouponItem.text = res.getString(R.string.homescreen_information_button_title)
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            homeItemThumbnail.setImageDrawable(res.getDrawable(R.drawable.point_history))
          }
          // TODO go to point history
          buttonCouponItem.setOnClickListener {
            val activity = itemView.context as AppCompatActivity
            val fragment = BranchCouponList()
              val transaction =  activity.supportFragmentManager.beginTransaction()
              transaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
              )
              transaction.replace(R.id.container, fragment)
              transaction.addToBackStack(null)
              transaction.commit()
          }
        }
      }


    }
  }

  inner class StickyHeaderSection (itemView: View): RecyclerView.ViewHolder(itemView) {
    private val headerTitle = itemView.textViewHeaderTitle
    private val loginButton = itemView.imageButtonStaffLogin

    fun bind() {
      val calendar = Calendar.getInstance()
      val currentTime = calendar.get(Calendar.HOUR_OF_DAY)
      val res = itemView.context

      when (currentTime) {
        in 0..12 -> {
          val title = res.getString(R.string.header_title_morning)
          headerTitle.text = res.getString(R.string.header_welcome, title)
        }
        in 12..16 -> {
          val title = res.getString(R.string.header_title_afternoon)
          headerTitle.text = res.getString(R.string.header_welcome, title)
        }
        in 16..21 -> {
          val title = res.getString(R.string.header_title_night)
          headerTitle.text = res.getString(R.string.header_welcome, title)
        }
        in 21..24 -> {
          val title = res.getString(R.string.header_title_night)
          headerTitle.text = res.getString(R.string.header_welcome, title)
        }

      }

      // TODO add action to go to staff login
      loginButton.setOnClickListener {

      }

    }
  }

  inner class HomeScreenHeader constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val imageViewHeader = itemView.imageViewHeader
    fun bind() {
      Glide.with(itemView)
        .load(Uri.parse(itemView.context.getString(R.string.header_image_url)))
        .into(imageViewHeader)
    }
  }

  class HomeScreenPoints constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val pointTitle = itemView.textViewPointTitle
    private val currentPoints = itemView.textViewCurrentPoints
    private val totalPoints = itemView.textViewTotalPoints
    private val imageRanking = itemView.imageViewRanking
    private val rankingTitle = itemView.textViewRanking
    private val res = itemView.context

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    fun bind() {
      val userTotalPoints = UserSingleton.instance.getTotalPoints()
      currentPoints.text = UserSingleton.instance.getCurrentPoints().toString()
      totalPoints.text = res.getString(R.string.homescreen_point_up_to_next_rank, userTotalPoints)

      when(userTotalPoints) {
        in (0..1999) -> {
          rankingTitle.text = "Bronze"
          imageRanking.setImageDrawable(res.getDrawable(R.drawable.bronze))
        }
        in 2000..6999 -> {
          rankingTitle.text = "Silver"
          imageRanking.setImageDrawable(res.getDrawable(R.drawable.silver))
        }
        in 7000..9999 -> {
          rankingTitle.text = "Gold"
          imageRanking.setImageDrawable(res.getDrawable(R.drawable.gold))
        }
        else -> {
          rankingTitle.text = "Platinum"
          imageRanking.setImageDrawable(res.getDrawable(R.drawable.ichiban))
        }

      }
    }

  }

  inner class HomeScreenFourthSection constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val title = itemView.textViewTitle
    private val heading = itemView.textViewHeading
    private val subheading = itemView.textViewSubHeading
    private val buttonRedirect = itemView.buttonRedirect

    fun bind() {
      buttonRedirect.setOnClickListener {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(itemView.context.getString(R.string.home_link_notice))
        itemView.context.startActivity(intent)
      }

    }

  }

  inner class HomeScreenFifthSection constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val imageView = itemView.imageViewRestaurant

    fun bind() {
      imageView.setOnClickListener {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://store.shopping.yahoo.co.jp/ra-mensekai/suspend.html")
        itemView.context.startActivity(intent)
      }
//      Picasso.get()
//        .load(Uri.parse(category.imageURL))
//        .into(categoryIcon)
    }

  }

  companion object {
    enum class TYPE {
      NOTICE, POINT_HISTORY
    }
  }

}



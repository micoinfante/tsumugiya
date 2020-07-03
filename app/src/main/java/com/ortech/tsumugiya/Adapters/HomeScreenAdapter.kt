package com.ortech.tsumugiya.Adapters

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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ortech.tsumugiya.*
import com.ortech.tsumugiya.Models.CMSSettings
import com.ortech.tsumugiya.Models.PointHistory
import com.ortech.tsumugiya.Models.WebsiteInfo
import com.ortech.tsumugiya.Models.UserSingleton
import com.ortech.tsumugiya.R
import kotlinx.android.synthetic.main.fragment_home_fifth_section.view.*
import kotlinx.android.synthetic.main.fragment_home_fourth_section.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_header.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_item.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_points.view.*
import kotlinx.android.synthetic.main.fragment_sticky_header.view.*
import java.util.*

class HomeScreenAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

  private val itemCount = 6
  private val points = Pair("current" to 0, "total" to 0)
  private var websiteData: WebsiteInfo? = null

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

  fun setWebsiteData(websiteData: WebsiteInfo) {
    this.websiteData = websiteData
    notifyDataSetChanged()
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when(position) {
      0 -> (holder as HomeScreenAdapter.StickyHeaderSection).bind()
      1 -> (holder as HomeScreenAdapter.HomeScreenHeader).bind()
      2 -> (holder as HomeScreenPoints).bind()
      3 -> (holder as HomeScreenItem).bind(TYPE.NOTICE)
      4 -> (holder as HomeScreenFourthSection).bind()
      5 -> (holder as HomeScreenItem).bind(TYPE.POINT_HISTORY)
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

    @SuppressLint("NewApi")
    fun bind(type: Companion.TYPE) {
      when (type) {
        Companion.TYPE.NOTICE -> {
          homeItemTitle.text = res.getString(R.string.homescreen_notice_title)
          homeItemSubtitle.text = res.getString(R.string.homescreen_notice_subtitle)
          buttonCouponItem.text = res.getString(R.string.homescreen_notice_button_title)

          Glide.with(itemView)
            .load(res.getDrawable(R.drawable.coupon_icon))
            .into(homeItemThumbnail)

          buttonCouponItem.setOnClickListener {
            val intent = Intent(itemView.context, BranchCouponList::class.java)
            itemView.context.startActivity(intent)
          }
        }
        Companion.TYPE.POINT_HISTORY -> {
          homeItemTitle.text = res.getString(R.string.homescreen_point_history_title)
          homeItemSubtitle.text = res.getString(R.string.homescreen_point_history_subtitle)
          buttonCouponItem.text = res.getString(R.string.homescreen_information_button_title)

          Glide.with(itemView)
            .load(res.getDrawable(R.drawable.point_history))
            .into(homeItemThumbnail)

          buttonCouponItem.setOnClickListener {
            val intent = Intent(itemView.context, PointHistoryActivity::class.java)
            itemView.context.startActivity(intent)
          }
        }
      }


    }
  }

  inner class StickyHeaderSection (itemView: View): RecyclerView.ViewHolder(itemView) {
    private val headerTitle = itemView.textViewHeaderTitle

    fun bind() {
      val calendar = Calendar.getInstance()
      val currentTime = calendar.get(Calendar.HOUR_OF_DAY)
      val res = itemView.context

      val username = UserSingleton.instance.name
      var userLoggedIn = true

      if (username == "") {
        userLoggedIn = false
      }
      if (username == null) {
        userLoggedIn = false
      }
      if (username == "default") {
        userLoggedIn = false
      }


      when (currentTime) {
        in 0..12 -> {
          val title = res.getString(R.string.header_title_morning)
          if (!userLoggedIn) {
            headerTitle.text = res.getString(R.string.header_welcome, title)
          } else {
            headerTitle.text = res.getString(R.string.header_title_night_loggedin, username)
          }

        }
        in 12..16 -> {
          val title = res.getString(R.string.header_title_afternoon)
          if (!userLoggedIn) {
            headerTitle.text = res.getString(R.string.header_welcome, title)
          } else {
            headerTitle.text = res.getString(R.string.header_title_afternoon_loggedin, username)
          }
        }
        in 16..21 -> {
          val title = res.getString(R.string.header_title_night)
          if (!userLoggedIn) {
            headerTitle.text = res.getString(R.string.header_welcome, title)
          } else {
            headerTitle.text = res.getString(R.string.header_title_night_loggedin, username)
          }
        }
        in 21..24 -> {
          val title = res.getString(R.string.header_title_night)
          if (userLoggedIn) {
            headerTitle.text = res.getString(R.string.header_welcome, title)
          } else {
            headerTitle.text = res.getString(R.string.header_title_night_loggedin, username)
          }
        }

      }

    }

  }

  inner class HomeScreenHeader constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val imageViewHeader = itemView.imageViewHeader
    fun bind() {
      Glide.with(itemView)
        .load(Uri.parse(itemView.context.getString(R.string.header_image_url)))
        .into(imageViewHeader)

      imageViewHeader.setOnClickListener {
        Log.d(Companion.TAG, "Homescreen header")
      }
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
      val userTotalPoints = UserSingleton.instance.getCurrentPoints()
      currentPoints.text = UserSingleton.instance.getCurrentPoints().toString()

      val username = UserSingleton.instance.name

      var userLoggedIn = true

      if (username == "") {
        userLoggedIn = false
      }
      if (username == null) {
        userLoggedIn = false
      }
      if (username == "default") {
        userLoggedIn = false
      }

      if (userLoggedIn) {
        pointTitle.text = res.getString(R.string.homescreen_point_section_welcome_loggedin, username)
      } else {
        pointTitle.text = res.getString(R.string.homescreen_point_section_welcome)
      }

      parsePoints()

      when(userTotalPoints) {
        in (0..499) -> {
          rankingTitle.text = "Bronze"
          val currentRankPoints = 500-userTotalPoints
          totalPoints.text = res.getString(R.string.homescreen_point_up_to_next_rank, currentRankPoints)
          Glide.with(itemView)
            .load(res.getDrawable(R.drawable.bronze))
            .into(imageRanking)
        }
        in 500..999 -> {
          rankingTitle.text = "Silver"
          val currentRankPoints = 1000-userTotalPoints
          totalPoints.text = res.getString(R.string.homescreen_point_up_to_next_rank, currentRankPoints)
          Glide.with(itemView)
            .load(res.getDrawable(R.drawable.silver))
            .into(imageRanking)
        }
        in 999..1999 -> {
          rankingTitle.text = "Gold"
          val currentRankPoints = 2000-userTotalPoints
          totalPoints.text = res.getString(R.string.homescreen_point_up_to_next_rank, currentRankPoints)
          Glide.with(itemView)
            .load(res.getDrawable(R.drawable.gold))
            .into(imageRanking)
        }
        else -> {
          rankingTitle.text = "Gold"
          val currentRankPoints = 2000-userTotalPoints
          totalPoints.text = res.getString(R.string.homescreen_point_up_to_next_rank, currentRankPoints)
          Glide.with(itemView)
            .load(res.getDrawable(R.drawable.gold))
            .into(imageRanking)
        }

      }
    }

    private fun parsePoints() {
      val rankings = CMSSettings.instance.rankings
      if (rankings.isNotEmpty()) {
        rankings.sortBy {it.rankPoints?.toInt()}
        Log.d(TAG, "Rankings: $rankings")
      }
    }

  }

  inner class HomeScreenFourthSection constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val title = itemView.textViewTitle
    private val heading = itemView.textViewHeading
    private val subheading = itemView.textViewSubHeading
    private val buttonRedirect = itemView.buttonRedirect

    fun bind() {

      websiteData?.let { websiteInfo ->
        heading.text = websiteInfo.websub
        subheading.text = websiteInfo.webMessage

        buttonRedirect.setOnClickListener {
//          val intent = Intent(itemView.context, WebViewActivity::class.java)
//          intent.putExtra(WebViewActivity.ARG_URL,  websiteInfo.webLink)
//          itemView.context.startActivity(intent)
          val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteInfo.webLink))
          itemView.context.startActivity(intent)
        }
      }



    }

  }

  inner class HomeScreenFifthSection constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val imageView = itemView.imageViewRestaurant

    fun bind() {
      imageView.setOnClickListener {
//        val intent = Intent(itemView.context, WebViewActivity::class.java)
//        intent.putExtra(WebViewActivity.ARG_URL, "https://store.shopping.yahoo.co.jp/ra-mensekai/")
//        itemView.context.startActivity(intent)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://store.shopping.yahoo.co.jp/ra-mensekai/"))
        itemView.context.startActivity(intent)
      }
    }

  }

  companion object {
    const val TAG = "HomeScreenAdapter"

    enum class TYPE {
      NOTICE, POINT_HISTORY
    }
  }

}



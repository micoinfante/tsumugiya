package com.ortech.shopapp.Adapters

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.common.io.Resources.getResource
import com.ortech.shopapp.BranchCouponList
import com.ortech.shopapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home_fifth_section.view.*
import kotlinx.android.synthetic.main.fragment_home_fourth_section.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_header.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_item.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_points.view.*
import kotlinx.android.synthetic.main.fragment_sticky_header.view.*
import java.util.*

class HomeScreenAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

  private val itemCount = 7

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

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when(position) {
      0 -> (holder as StickyHeaderSection).bind()
      1 -> (holder as HomeScreenHeader).bind()
      2 -> (holder as HomeScreenPoints).bind()
      3 -> (holder as HomeScreenItem).bind()
      4 -> (holder as HomeScreenFourthSection).bind()
      5 -> (holder as HomeScreenFifthSection).bind()
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

    fun bind() {
      buttonCouponItem.setOnClickListener {
        val activity = itemView.context as AppCompatActivity
        val fragment = BranchCouponList()
        val transaction =  activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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
          headerTitle.text = res.getString(R.string.header_title_morning)
        }
        in 12..16 -> {
          headerTitle.text = res.getString(R.string.header_title_afternoon)
        }
        in 16..21 -> {
          headerTitle.text = res.getString(R.string.header_title_night)
        }
        in 21..24 -> {
          headerTitle.text = res.getString(R.string.header_title_night)
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

    fun bind() {
      currentPoints.text = 324.toString()
      totalPoints.text = 444.toString()
      rankingTitle.text = "Bronze"
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
        intent.data = Uri.parse("https://facebook.com")
        itemView.context.startActivity(intent)
      }
//      Picasso.get()
//        .load(Uri.parse(category.imageURL))
//        .into(categoryIcon)
    }

  }

}



package com.ortech.shopapp.Adapters

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.common.io.Resources.getResource
import com.ortech.shopapp.BranchCouponList
import com.ortech.shopapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home_fifth_section.view.*
import kotlinx.android.synthetic.main.fragment_home_fourth_section.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_item.view.*

class HomeScreenAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    Log.d("HomeScreen", "Setting Up Sections")
    return when(viewType) {
      0 -> {
        Log.d("HomeScreen", "Setup Header")
        HomeScreenHeader(
          inflater.inflate(R.layout.fragment_home_screen_header, parent, false)
        )
      }
      1 -> {
        Log.d("HomeScreen", "Setup Points")
        HomeScreenPoints(
          inflater.inflate(R.layout.fragment_home_screen_points, parent, false)
        )
      }
      3 -> {
        HomeScreenFourthSection(inflater.inflate(R.layout.fragment_home_fourth_section, parent, false))
      }
      4  -> {
        HomeScreenFifthSection(inflater.inflate(R.layout.fragment_home_fifth_section, parent, false))
      }

      else -> {
        HomeScreenItem(
          inflater.inflate(R.layout.fragment_home_screen_item, parent, false)
        )
      }
    }

  }

  override fun onBindViewHolder(
    holder: RecyclerView.ViewHolder,
    position: Int,
    payloads: MutableList<Any>
  ) {
    super.onBindViewHolder(holder, position, payloads)
    when(position) {
      2 -> (holder as HomeScreenItem).bind()
      3 -> (holder as HomeScreenFourthSection).bind()
      4 -> (holder as HomeScreenFifthSection).bind()
    }
  }

  override fun getItemViewType(position: Int): Int {
    return position
  }

  override fun getItemCount(): Int {
    return 6
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

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

  class HomeScreenHeader constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

  }

  class HomeScreenPoints constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

  }

  inner class HomeScreenFourthSection constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val title = itemView.textViewTitle
    private val heading = itemView.textViewHeading
    private val subheading = itemView.textViewSubHeading
    private val buttonRedirect = itemView.buttonRedirect

    fun bind() {
      buttonRedirect.setOnClickListener {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://google.com")
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



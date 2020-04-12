package com.ortech.shopapp.Adapters

import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.ortech.shopapp.Models.MenuCategory
import com.ortech.shopapp.Models.Store
import com.ortech.shopapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home_screen_header.view.*
import kotlinx.android.synthetic.main.fragment_menu_item.view.*
import kotlinx.android.synthetic.main.fragment_store_item.view.*

class MenuCategoryAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private var categories: ArrayList<MenuCategory> = arrayListOf<MenuCategory>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return MenuCategoryViewHolder(
      inflater.inflate(R.layout.fragment_menu_item, parent, false)
    )
  }

  override fun getItemViewType(position: Int): Int {
    return position
  }

  override fun getItemCount(): Int {
    return categories.size
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when(holder) {
      is MenuCategoryViewHolder -> {
        holder.apply {
          Log.d(TAG, "New Store: $position")
          val category = categories[position]
          holder.bind(category)
        }
      }
    }
  }

  fun updateData(categories: ArrayList<MenuCategory>) {
    this.categories = categories
    notifyDataSetChanged()
  }


  class MenuCategoryViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val categoryLabel = itemView.tvMenuCategory
    private val categoryIcon = itemView.imageViewMenuCategory

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bind(category: MenuCategory){
      categoryLabel.text = category.name
      categoryIcon.clipToOutline = true
      Picasso.get()
        .load(Uri.parse(category.imageURL))
        .into(categoryIcon)

    }
  }

  companion object {
    const val TAG = "StoreListAdapter"
  }
}
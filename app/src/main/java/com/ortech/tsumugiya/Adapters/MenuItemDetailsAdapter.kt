package com.ortech.tsumugiya.Adapters

import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.ortech.tsumugiya.Models.Branch
import com.ortech.tsumugiya.Models.MenuList
import com.ortech.tsumugiya.Models.UserSingleton
import com.ortech.tsumugiya.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.branch_details_section.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_header.view.*
import kotlinx.android.synthetic.main.menu_item_detail_section.view.*

class MenuItemDetailsAdapter (private val menuItem: MenuList): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private var itemCount = 2

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return if (viewType == 0) {
      MenuItemHeaderViewHolder(
        inflater.inflate(
          R.layout.fragment_home_screen_header,
          parent,
          false
        )
      )
    } else MenuItemDetailsViewHolder(
      inflater.inflate(
        R.layout.menu_item_detail_section,
        parent,
        false
      )
    )
  }

  override fun getItemViewType(position: Int): Int {
    return if (position == 0) {
      TYPE_HEADER
    } else TYPE_DETAIL
  }

  override fun getItemCount(): Int {
    return itemCount
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is MenuItemDetailsAdapter.MenuItemHeaderViewHolder -> holder.bind()
      is MenuItemDetailsAdapter.MenuItemDetailsViewHolder -> holder.bind()
    }
  }

  inner class MenuItemHeaderViewHolder constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.imageViewHeader

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bind() {
      Picasso.get()
        .load(Uri.parse(menuItem.imageURL))
        .into(imageView)
    }

  }

  inner class MenuItemDetailsViewHolder constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private val title = itemView.textViewMenuItemLabel
    private val price = itemView.textViewMenuItemPrice
    private val description = itemView.textViewMenuItemDescription
    private val res = itemView.context.resources

    fun bind() {
      title.text = menuItem.menuLabel
      price.text = res.getString(R.string.yen, menuItem.menuPrice)
      description.text = menuItem.menuDetails
      // ToDo AddAction to button
    }
  }

  companion object {
    const val TYPE_HEADER = 0
    const val TYPE_DETAIL = 1
  }

}
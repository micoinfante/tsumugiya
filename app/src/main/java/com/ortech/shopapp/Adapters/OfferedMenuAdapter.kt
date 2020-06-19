package com.ortech.shopapp.Adapters

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ortech.shopapp.MenuItemDetails
import com.ortech.shopapp.Models.Branch
import com.ortech.shopapp.Models.MenuList
import com.ortech.shopapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home_screen_header.view.*
import kotlinx.android.synthetic.main.fragment_menu_item.view.*
import kotlinx.android.synthetic.main.fragment_menu_list_item.view.*

class OfferedMenuAdapter(private val branch: Branch):
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private var menuList: ArrayList<MenuList> = arrayListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return if (viewType == 0) {
      BranchHeaderHolder(inflater.inflate(R.layout.fragment_home_screen_header, parent, false))
    } else {
      MenuItemViewHolder(
        inflater.inflate(R.layout.fragment_menu_item, parent, false)
      )
    }
  }

  override fun getItemCount(): Int {
    // + 1 for the header
    return menuList.size + 1
  }

  fun setOfferedMenu(offeredMenuItems: ArrayList<MenuList>) {
    this.menuList = offeredMenuItems
    notifyDataSetChanged()
  }

  override fun getItemViewType(position: Int): Int {
    return if (position == 0) {
      TYPE_HEADER
    } else {
      TYPE_MENU_ITEM
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when(holder) {
      is BranchHeaderHolder -> holder.bind()
      is MenuItemViewHolder -> {
        val item = menuList[position]
        holder.bind(item)
      }
    }
  }

  inner class BranchHeaderHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.imageViewHeader

    fun bind() {
      Picasso.get()
        .load(Uri.parse(branch.branchURLImages))
        .into(imageView)
    }

  }

  inner class MenuItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val menuLabel = itemView.tvMenuCategory
    private val menuImage = itemView.imageViewMenuCategory
    private val res = itemView.context
    private val card = itemView.cardViewMenuCategory

    fun bind(item: MenuList) {
      card.setBackgroundColor(ContextCompat.getColor(res, R.color.primary_background))
      menuLabel.setTextColor(Color.WHITE)
      menuLabel.text = item.menuLabel
      Picasso.get()
        .load(Uri.parse(item.imageURL))
        .into(menuImage)
      itemView.setOnClickListener {
        val activity = itemView.context as AppCompatActivity
        val fragment = MenuItemDetails.newInstance(item)
        val transaction =  activity.supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
          R.anim.enter_from_left,
          R.anim.exit_to_left,
          R.anim.enter_from_left,
          R.anim.exit_to_left
        )
        transaction.add(R.id.container, fragment)
        transaction.addToBackStack("OfferedMenu")
        transaction.commit()
      }
    }
  }

  companion object {
    const val TAG = "OfferedMenuAdapter"
    const val TYPE_HEADER = 0
    const val TYPE_MENU_ITEM = 1
  }

}
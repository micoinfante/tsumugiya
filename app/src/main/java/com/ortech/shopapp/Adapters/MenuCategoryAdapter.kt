package com.ortech.shopapp.Adapters

import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ortech.shopapp.BranchCouponList
import com.ortech.shopapp.MenuItemDetails
import com.ortech.shopapp.MenuListFragment
import com.ortech.shopapp.Models.MenuCategory
import com.ortech.shopapp.Models.MenuList
import com.ortech.shopapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_menu_item.view.*
import kotlinx.android.synthetic.main.fragment_menu_list_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class MenuCategoryAdapter (private val type: MenuType = MenuType.Category): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private var categories: ArrayList<MenuCategory> = arrayListOf<MenuCategory>()
  private var menuList: ArrayList<MenuList> = arrayListOf<MenuList>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)



    return if(this.type == MenuType.Category) {
      MenuCategoryViewHolder(
        inflater.inflate(R.layout.fragment_menu_item, parent, false)
      )
    } else {
      val listView =  inflater.inflate(R.layout.fragment_menu_list_item, parent, false)
      val params = listView.layoutParams as GridLayoutManager.LayoutParams
      params.width = parent.measuredWidth / 2
      listView.layoutParams = params
      MenuListViewHolder(listView)
    }
  }

  override fun getItemViewType(position: Int): Int {
    return position
  }

  override fun getItemCount(): Int {
    return if(this.type == MenuType.Category) {
      categories.size
    } else menuList.size
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when(holder) {
      is MenuCategoryViewHolder -> {
          val category = categories[position]
          holder.bind(category)
      }
      is MenuListViewHolder -> {
          val menuItem = menuList[position]
          holder.bind(menuItem)
      }
    }
  }

  fun updateCategoryData(categories: ArrayList<MenuCategory>) {
    this.categories = categories
    notifyDataSetChanged()
  }

  fun updateMenuData(menuList: ArrayList<MenuList>) {
    this.menuList = menuList
    notifyDataSetChanged()
  }


  class MenuCategoryViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val categoryLabel = itemView.tvMenuCategory
    private val categoryIcon = itemView.imageViewMenuCategory

    fun bind(category: MenuCategory){
      categoryLabel.text = category.categoryLabel
      Picasso.get()
        .load(Uri.parse(category.imageURL))
        .into(categoryIcon)

      itemView.setOnClickListener {
        val activity = itemView.context as AppCompatActivity
        val fragment = MenuListFragment.newInstance(category)
        val transaction =  activity.supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
          R.anim.enter_from_left,
          R.anim.exit_to_left,
          R.anim.enter_from_left,
          R.anim.exit_to_left
        )
        transaction.add(R.id.container, fragment)
        transaction.addToBackStack("MenuListFragment")
        transaction.commit()
      }

    }
  }

  inner class MenuListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val menuLabel = itemView.textViewMenuListItem
    private val menuImage = itemView.imageViewMenuListItem


    fun bind(list: MenuList) {
      menuLabel.text = list.menuLabel
      Picasso.get()
        .load(Uri.parse(list.imageURL))
        .into(menuImage)
      itemView.setOnClickListener {
        val activity = itemView.context as AppCompatActivity
        val fragment = MenuItemDetails.newInstance(list)
        val transaction =  activity.supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
          R.anim.enter_from_left,
          R.anim.exit_to_left,
          R.anim.enter_from_left,
          R.anim.exit_to_left
        )
        transaction.add(R.id.container, fragment)
        transaction.addToBackStack("MenuListFragment")
        transaction.commit()
      }
    }
  }

  companion object {
    const val TAG = "StoreListAdapter"
  }

}
enum class MenuType{
  Category, List
}
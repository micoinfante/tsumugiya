package com.ortech.shopapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.MenuCategoryAdapter
import com.ortech.shopapp.Adapters.MenuType
import com.ortech.shopapp.Models.MenuCategory
import com.ortech.shopapp.Models.MenuList
import com.ortech.shopapp.Views.GridSpacingItemDecoration
import com.ortech.shopapp.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.fragment_menu.*


const val ARGS_MENU_CATEGORY = "menu"

class MenuListFragment() : Fragment() {


  private var menuCategory: MenuCategory? = null
  private val menuList: ArrayList<MenuList> = ArrayList<MenuList>()
  private lateinit var menuListAdapter: MenuCategoryAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
    arguments?.let {
      menuCategory = it.getSerializable(ARGS_MENU_CATEGORY) as? MenuCategory
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_menu, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setup()
  }

  private fun setup() {
    menuListAdapter = MenuCategoryAdapter(MenuType.List)
    val recyclerView = menuCategoryRecyclerView
    recyclerView.apply {
      layoutManager = GridLayoutManager(this.context,2)
//      addItemDecoration(GridSpacingItemDecoration(2, 20, true))
      recyclerView.adapter = menuListAdapter
    }
    getMenuList()
  }

  private fun getMenuList() {
    val db = Firebase.firestore
    menuCategory?.let {
      db.collection("CMSMenuList")
        .whereEqualTo("categoryID", it.categoryID)
        .get()
        .addOnSuccessListener { querySnapshot ->
          querySnapshot.forEach {queryDocumentSnapshot ->
            val newMenuListItem = queryDocumentSnapshot.toObject(MenuList::class.java)
            menuList.add(newMenuListItem)
            dataSetUpdated()
          }
        }
    }
  }

  private fun dataSetUpdated() {
    menuListAdapter.updateMenuData(this.menuList)
  }


  companion object {
    const val TAG = "MenuListFragment"

    @JvmStatic
    fun newInstance(menuCategory: MenuCategory):MenuListFragment = MenuListFragment().apply {
      arguments = Bundle().apply {
        putSerializable(ARGS_MENU_CATEGORY, menuCategory)
      }
    }

  }

}

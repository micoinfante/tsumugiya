package com.ortech.tsumugiya

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.tsumugiya.Adapters.MenuCategoryAdapter
import com.ortech.tsumugiya.Adapters.MenuType
import com.ortech.tsumugiya.Models.MenuCategory
import com.ortech.tsumugiya.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.fragment_branch_list.*
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.fragment_menu.menuCategoryRecyclerView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {
  // TODO: Rename and change types of parameters
  private var param1: String? = null
  private var param2: String? = null

  private var db = Firebase.firestore
  private var menuCategories: ArrayList<MenuCategory> = ArrayList()
  private lateinit var menuCategoryAdapter: MenuCategoryAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
    arguments?.let {
      param1 = it.getString(ARG_PARAM1)
      param2 = it.getString(ARG_PARAM2)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setup()
    getMenuCategory()

  }


  private fun setup() {
    menuCategoryAdapter =
        MenuCategoryAdapter(MenuType.Category)
    val recyclerView = menuCategoryRecyclerView
    recyclerView.apply {
      layoutManager = LinearLayoutManager(this@MenuFragment.context)
      addItemDecoration(TopSpacingDecoration(6))
      recyclerView.adapter = menuCategoryAdapter
    }
  }

  private fun setupToolBar() {
    val toolbar = toolbarMenu
    toolbar.setNavigationOnClickListener {
      activity?.supportFragmentManager?.popBackStack()
    }
  }

  private fun getMenuCategory() {
    db.collection("CMSMenuCategory")
      .orderBy("orderBy", Query.Direction.DESCENDING)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          val newMenuCategory = document.toObject(MenuCategory::class.java)
          this.menuCategories.add(newMenuCategory)
          menuCategoryAdapter.updateCategoryData(this.menuCategories)
        }
      }
      .addOnFailureListener {exception ->
        Log.w(TAG, "Error getting documents.", exception)
      }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_menu, container, false)
  }

  companion object {
    const val TAG = "MenuFragment"
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    @JvmStatic
    fun newInstance(param1: String, param2: String) =
      MenuFragment().apply {
        arguments = Bundle().apply {
          putString(ARG_PARAM1, param1)
          putString(ARG_PARAM2, param2)
        }
      }
  }
}

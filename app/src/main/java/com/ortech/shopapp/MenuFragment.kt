package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.MenuCategoryAdapter
import com.ortech.shopapp.Models.MenuCategory
import com.ortech.shopapp.Models.Store
import com.ortech.shopapp.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.fragment_menu.*


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
    menuCategoryAdapter = MenuCategoryAdapter()
    val recyclerView = menuCategoryRecyclerView
    recyclerView.apply {
      layoutManager = LinearLayoutManager(this@MenuFragment.context)
      addItemDecoration(TopSpacingDecoration(6))
      recyclerView.adapter = menuCategoryAdapter
    }
  }

  private fun getMenuCategory() {
    db.collection("CMSMenuCategory").get()
      .addOnSuccessListener { result ->
        for (document in result) {
          val newMenuCategory = document.toObject(MenuCategory::class.java)
          this.menuCategories.add(newMenuCategory)
          menuCategoryAdapter.updateData(this.menuCategories)
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

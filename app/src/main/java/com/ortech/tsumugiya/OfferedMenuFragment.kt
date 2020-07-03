package com.ortech.tsumugiya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.tsumugiya.Adapters.MenuCategoryAdapter
import com.ortech.tsumugiya.Adapters.MenuType
import com.ortech.tsumugiya.Adapters.OfferedMenuAdapter
import com.ortech.tsumugiya.Models.Branch
import com.ortech.tsumugiya.Models.MenuList
import com.ortech.tsumugiya.Views.HeaderItemDecoration
import kotlinx.android.synthetic.main.fragment_branch_details.*
import kotlinx.android.synthetic.main.fragment_menu.*


class OfferedMenu: Fragment() {

  private var branch: Branch? = null
  private var db = Firebase.firestore
  private var offeredMenuItems: ArrayList<MenuList> = arrayListOf()
  private lateinit var offeredMenuAdapter: OfferedMenuAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    arguments?.let {
      branch = it.getSerializable(ARGS_BRANCH) as Branch
    }
  }


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_branch_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setup()
    getOfferedMenu()
  }

  override fun onResume() {
    super.onResume()
    setup()
    getOfferedMenu()
  }

  private fun setup() {
    offeredMenuAdapter = branch?.let {
       OfferedMenuAdapter(
            it
        )
    }!!
    val recyclerView = recyclerViewBranchDetails
    recyclerView.apply {
      layoutManager = LinearLayoutManager(this.context)
      addItemDecoration(HeaderItemDecoration(recyclerView, false, isHeader()))
      recyclerView.adapter = offeredMenuAdapter
    }
  }

  private fun getOfferedMenu() {
    offeredMenuItems.clear()
    branch?.let {
      db.collection("CMSMenuList")
        .whereArrayContains("selectedBranch", it.branchID)
        .get()
        .addOnSuccessListener { querySnapshot ->
          querySnapshot.forEach {queryDocumentSnapshot ->
            val newMenuListItem = queryDocumentSnapshot.toObject(MenuList::class.java)
            offeredMenuItems.add(newMenuListItem)
            updateDataSet()
          }
        }
    }
  }

  private fun updateDataSet() {
    offeredMenuAdapter.setOfferedMenu(this.offeredMenuItems)
  }

  private fun isHeader() : (itemPosition: Int) -> Boolean {
    return {
        itemPosition ->
      itemPosition == 0
    }
  }

  companion object {
    const val TAG = "OfferedMenu"

    @JvmStatic
    fun newInstance(branch: Branch) = OfferedMenu().apply {
      arguments = Bundle().apply {
        putSerializable(ARGS_BRANCH, branch)
      }
    }
  }


}
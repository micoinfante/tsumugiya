package com.ortech.tsumugiya

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.tsumugiya.Adapters.StoreOfferingItemAdapter
import com.ortech.tsumugiya.Models.Branch
import com.ortech.tsumugiya.Models.Coupon
import com.ortech.tsumugiya.Models.MenuList
import kotlinx.android.synthetic.main.activity_store_offering_item.*

class StoreOfferingItemActivity : AppCompatActivity() {

  private var menuItem: MenuList? = null
  private var db = Firebase.firestore
  private var selectedBranchList: ArrayList<Branch> = arrayListOf()
  private lateinit var adapter: StoreOfferingItemAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_store_offering_item)

   menuItem = intent.getSerializableExtra(ARG_MENU_ITEM) as MenuList


    setup()
    setupToolbar()
  }

  private fun setup() {
    recyclerViewStoreOfferingItem.apply {
      layoutManager = LinearLayoutManager(this.context)
      recyclerViewStoreOfferingItem.adapter =
          StoreOfferingItemAdapter()
    }
    getStores()
  }

  private fun setupToolbar() {
    val toolbar = toolbarStoreOffering
    toolbar.setNavigationOnClickListener {
      finish()
    }
  }


  private fun getStores() {
    menuItem?.let { it->
      it.selectedBranch.forEach { branchID ->
          db.collection("CMSBranches")
            .whereEqualTo("branchID", branchID)
            .get()
            .addOnSuccessListener{ queryDocument ->
              if (queryDocument.count() != 0) {
                val branch = queryDocument.documents.first().toObject(Branch::class.java)
                if (branch != null) {
                  selectedBranchList.add(branch)
                  dataSetUpdated()
                }
              }
            }
      }
    }
  }

  private fun dataSetUpdated() {
   (recyclerViewStoreOfferingItem.adapter as StoreOfferingItemAdapter).updateDataSet(this.selectedBranchList)
  }




  companion object {
    const val TAG = "StoreOffering"
    const val ARG_MENU_ITEM = "menuItem"
  }

}
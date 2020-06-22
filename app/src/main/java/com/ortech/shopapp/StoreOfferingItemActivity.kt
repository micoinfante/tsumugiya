package com.ortech.shopapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.StoreOfferingItemAdapter
import com.ortech.shopapp.Models.Branch
import com.ortech.shopapp.Models.Coupon
import com.ortech.shopapp.Models.MenuList
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
    adapter = StoreOfferingItemAdapter()
    recyclerViewStoreOfferingItem.apply {
      layoutManager = LinearLayoutManager(this.context)
      recyclerViewStoreOfferingItem.adapter = this.adapter
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
      it.selectedBranches?.forEach { branchID ->
        db.collection("CMSBranch")
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
    adapter.updateDataSet(this.selectedBranchList)
  }




  companion object {
    const val TAG = "StoreOffering"
    const val ARG_MENU_ITEM = "menuItem"
  }

}
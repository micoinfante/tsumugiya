package com.ortech.tsumugiya

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.tsumugiya.Adapters.StoreListAdapter
import com.ortech.tsumugiya.Models.Branch
import com.ortech.tsumugiya.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.fragment_store_tab_list.*

private const val ARG_STORE_NAME = "storeName"
private const val ARG_ADDRESS = "address"
private const val ARG_PHONE = "phone"
private const val ARG_STORE_HOURS = "storeHours"


class StoreListFragment : Fragment() {
  // TODO: Rename and change types of parameters
  private var storeName: String? = null
  private var address: String? = null
  private var phone: String? = null
  private var storeHours: String? = null

  private var db = Firebase.firestore
  private var stores = arrayListOf<Branch>()
  private lateinit var storeListAdapter: StoreListAdapter

  private fun setup() {
    storeListAdapter = StoreListAdapter()
    val storeListRecyclerView = recyclerViewStoreList
    storeListRecyclerView.apply {
      layoutManager = LinearLayoutManager(this.context)
      val topSpacing = TopSpacingDecoration(8)
      addItemDecoration(topSpacing)
      storeListRecyclerView.adapter = storeListAdapter
    }

  }

  private fun getBranches() {
    stores.clear()
    db.collection("CMSBranches").get()
      .addOnSuccessListener { result ->
        for (document in result) {

          val newStore = document.toObject(Branch::class.java)
          this.stores.add(newStore)
          storeListAdapter.updateData(this.stores)
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
    return inflater.inflate(R.layout.fragment_store_tab_list, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setup()
    getBranches()
  }

  override fun onResume() {
    super.onResume()
//      getBranches()
  }

  fun getStores() : ArrayList<Branch> {
    return this.stores
  }


  companion object {
    private val TAG = "StoreListFragment"

    fun newInstance() =
      StoreListFragment().apply {
//        setup()
      }
  }


}

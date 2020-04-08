package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.StoreListAdapter
import com.ortech.shopapp.Models.Store
import com.ortech.shopapp.Views.TopSpacingDecoration
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
  private var stores = arrayListOf<Store>()

//  private lateinit var storeListRecyclerView: RecyclerView
  private lateinit var storeListAdapter: StoreListAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  private fun setup() {
    storeListAdapter = StoreListAdapter()
    val storeListRecyclerView = recyclerViewStoreList
    storeListRecyclerView.apply {
      layoutManager = LinearLayoutManager(this.context)
      val topSpacing = TopSpacingDecoration(8)
      addItemDecoration(topSpacing)
      storeListRecyclerView.adapter = storeListAdapter
    }
    getBranches()

  }

  private fun getBranches() {
    db.collection("CMSBranches").get()
      .addOnSuccessListener { result ->
        for (document in result) {

          val newStore = document.toObject(Store::class.java)
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
  }


  companion object {
    private val TAG = "StoreListFragment"

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param storeName Parameter 1.
     * @param address Parameter 2.
     * @return A new instance of fragment StoreListFragment.
     */
    // TODO: Rename and change types and number of parameters
    @JvmStatic
//    fun newInstance(storeName: String, address: String, phone: String, storeHours: String) =
//      StoreListFragment().apply {
//        arguments = Bundle().apply {
//          putString(ARG_STORE_NAME, storeName)
//          putString(ARG_ADDRESS, address)
//          putString(ARG_PHONE, phone)
//          putString(ARG_STORE_HOURS, storeHours)
//        }
//      }
//  }
    fun newInstance() =
      StoreListFragment().apply {
//        setup()
      }
  }


}

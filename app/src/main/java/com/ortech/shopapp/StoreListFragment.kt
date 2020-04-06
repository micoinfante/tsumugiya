package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      storeName = it.get(ARG_STORE_NAME) as String?
      address = it.getString(ARG_ADDRESS) as String?
      phone = it.get(ARG_PHONE) as String?
      storeHours = it.get(ARG_STORE_HOURS) as String?
    }

    getBranches()

  }

  private fun getBranches() {
    db.collection("CMSBranches").get()
      .addOnSuccessListener { result ->
        for (document in result) {
          Log.d(TAG, "${document.id}")
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
      }
  }


}

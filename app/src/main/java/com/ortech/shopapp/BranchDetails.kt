package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.BranchDetailsAdapter
import com.ortech.shopapp.Models.Branch
import com.ortech.shopapp.Views.HeaderItemDecoration
import kotlinx.android.synthetic.main.fragment_branch_details.*
import kotlinx.android.synthetic.main.fragment_store_tab_details.*
import java.util.*
import kotlin.collections.ArrayList

const val ARGS_BRANCH = "branch"

class BranchDetails : Fragment() {

  private var branch: Branch? = null
  private var couponsAvailable = false
  private var db = Firebase.firestore
  private var adapter: BranchDetailsAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    arguments?.let {
      branch = it.getSerializable(ARGS_BRANCH) as Branch
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_branch_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setup()
    checkRunningCoupons()
  }

  private fun setup() {

    branch?.let {
      recyclerViewBranchDetails.apply {
        this.adapter = BranchDetailsAdapter(it)
        addItemDecoration(HeaderItemDecoration(this, false, isHeader()))
        layoutManager = LinearLayoutManager(this.context)
      }
    }

  }

  private fun checkRunningCoupons() {
    Log.d(TAG, "Checking running coupons ${branch?.branchID ?: ""}")
//      .whereArrayContains("selectedBranch", branch?.branchID ?: "")
    db.collection("CMSCoupon")
      .whereGreaterThanOrEqualTo("untilDate", Timestamp(Date()))
      .get()
      .addOnSuccessListener { it ->
        val selectedBranchesArray = it.documents.map {
          it["selectedBranch"] as ArrayList<String>
        }.flatten()

        val selectedStoreNameArray = it.documents.map {
          it["selectedStoreName"] as ArrayList<String>
        }.flatten()

        Log.d(TAG, "SelectedBranches: ${selectedBranchesArray.toString()} " +
                "contains? ${branch?.branchID ?: ""}")
        if (selectedBranchesArray.contains(branch?.branchID ?: "") || selectedStoreNameArray.contains(branch?.branch ?: "")) {
          Log.d(TAG, "Got running coupons ${it!!.count()}")
          areCouponsAvailable(true)
        } else {
          areCouponsAvailable(false)
        }
      }
      .addOnFailureListener {
        areCouponsAvailable(false)
      }
  }

  private fun areCouponsAvailable(status: Boolean) {
    couponsAvailable = status
    Log.d(TAG, "changing coupons status")
    (recyclerViewBranchDetails.adapter as BranchDetailsAdapter).couponsAvailable(status)
  }

  private fun isHeader() : (itemPosition: Int) -> Boolean {
    return {
        itemPosition ->
      itemPosition == 0
    }
  }


  companion object {
    const val TAG = "BranchList"

    @JvmStatic
    fun newInstance(branch: Branch) = BranchDetails().apply {
      arguments = Bundle().apply {
        putSerializable(ARGS_BRANCH, branch)
      }
    }
  }

}

package com.ortech.shopapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.AllCouponListAdapter
import com.ortech.shopapp.Models.*
import com.ortech.shopapp.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.fragment_branch_coupon_list.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class BranchCouponList : AppCompatActivity() {

  private lateinit var couponBranchListAdapter: AllCouponListAdapter
  private val db = Firebase.firestore
  private var couponList = ArrayList<Coupon>()
  private var pointHistoryList = ArrayList<PointHistory>()
  private var storeCoupons = HashMap<String, ArrayList<Coupon>>()
  private var formattedCouponData: ArrayList<AdapterItem<Coupon>> = arrayListOf()
  private var currentBranch: Branch? = null
  private var couponListener: ListenerRegistration? = null
  private var redeemedCouponListener: ListenerRegistration? = null
  private var toCheckBranchID = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.fragment_branch_coupon_list)

   val data = intent.getSerializableExtra(ARG_BRANCH)
    if (data != null) {
      currentBranch = data as Branch
    }
    setup()
  }

  private fun setup() {
    couponBranchListAdapter = AllCouponListAdapter()
    branchCouponListRecyclerView.apply {
      layoutManager = LinearLayoutManager(this.context)
      val topSpacing = TopSpacingDecoration(20)
      addItemDecoration(topSpacing)
      branchCouponListRecyclerView.adapter = couponBranchListAdapter
    }

    this.baseContext?.let {
      ContextCompat.getColor(
        it, R.color.primary_orange)
    }?.let { swipeRefreshCouponList.setProgressBackgroundColorSchemeColor(it) }

    swipeRefreshCouponList.setColorSchemeColors(Color.WHITE)


    // for Swipe refresh
    swipeRefreshCouponList.setOnRefreshListener {
      formattedCouponData.clear()
      pointHistoryList.clear()
      updateData()
      removeAdapterData()
      branchCouponListRecyclerView.removeAllViewsInLayout()
      fetchCoupons()
    }

    fetchCoupons()
    setupToolBar()

  }

  private fun setupToolBar() {
    val toolbar = toolbarCouponList
    toolbar.setNavigationOnClickListener {
      finish()
    }
  }

  private fun removeAdapterData() {
    couponBranchListAdapter.removeAll()
  }

  private fun updateData() {
    couponBranchListAdapter.updateData(formattedCouponData)
    couponBranchListAdapter.updateTransactionData(pointHistoryList)
  }

  private fun fetchCoupons() {
    couponList.clear()
    var query: Query = db.collection("CMSCoupon")
      .whereGreaterThanOrEqualTo("untilDate", Timestamp(Date()))

    if (currentBranch != null) {
      currentBranch?.branchID?.let {branchID ->
        Log.d(TAG, "current branch: $branchID")
        query = db.collection("CMSCoupon")
          .whereGreaterThanOrEqualTo("untilDate", Timestamp(Date()))
//          .whereArrayContains("selectedBranch", branchID)
        querySpecificBranchCoupons(query)
        toCheckBranchID = true
      }

    } else {
      toCheckBranchID = false
      queryAllBranchCoupons(query)
    }
  }

  private fun queryAllBranchCoupons(query: Query) {
    couponListener = query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
      if (firebaseFirestoreException != null) {
        Log.e(TAG, "Can't get branch coupons ${firebaseFirestoreException.toString()}")
      } else {
        Log.d(TAG, "Branch coupons ${querySnapshot!!.count()}")
        querySnapshot?.documentChanges?.forEach {documentChange ->
          val newCoupon: Coupon?
          when(documentChange.type) {
            DocumentChange.Type.ADDED -> {
              newCoupon = documentChange.document.toObject(Coupon::class.java)
              couponList.add(newCoupon)
            }
            DocumentChange.Type.MODIFIED -> {
              newCoupon = documentChange.document.toObject(Coupon::class.java)
              val index = couponList.indexOfFirst {
                it.couponID == newCoupon.couponID
              }
              couponList[index] = newCoupon
            }
            DocumentChange.Type.REMOVED -> {
              newCoupon = documentChange.document.toObject(Coupon::class.java)
              val index = couponList.indexOfFirst {
                it.couponID == newCoupon.couponID
              }
              couponList.removeAt(index)
            }
          } // END: Document type check
          fetchRedeemedCoupons()
        } // END: Document loop
      } // END: Else
    } // END: Query
  }

  private fun querySpecificBranchCoupons(query: Query) {
    couponListener = query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
      if (firebaseFirestoreException != null) {
        Log.e(TAG, "Can't get branch coupons ${firebaseFirestoreException.toString()}")
      } else {
        Log.d(TAG, "Branch[${currentBranch!!.branch}] coupons ${querySnapshot!!.count()}")

        querySnapshot?.documentChanges?.forEach {documentChange ->
          val newCoupon: Coupon?
          when(documentChange.type) {
            DocumentChange.Type.ADDED -> {
              newCoupon = documentChange.document.toObject(Coupon::class.java)
              newCoupon.selectedStoreName?.let {
                if (it.contains(currentBranch?.branch ?: "")) {
                  couponList.add(newCoupon)
                }
              }
            }
            DocumentChange.Type.MODIFIED -> {
              newCoupon = documentChange.document.toObject(Coupon::class.java)
              newCoupon.selectedStoreName?.let {
                if (it.contains(currentBranch?.branch ?: "")) {
                  val index = couponList.indexOfFirst {currentCoupon ->
                    currentCoupon.couponID == newCoupon.couponID
                  }
                  couponList[index] = newCoupon
                }
              }
            }
            DocumentChange.Type.REMOVED -> {
              newCoupon = documentChange.document.toObject(Coupon::class.java)
              newCoupon.selectedStoreName?.let {
                if (it.contains(currentBranch?.branch ?: "")) {
                  val index = couponList.indexOfFirst {
                    it.couponID == newCoupon.couponID
                  }
                  couponList.removeAt(index)
                }
              }
            }
          } // END: Document type check
          fetchRedeemedCoupons()
        } // END: Document loop
      } // END: Else
    } // END: Query
  }


  private fun fetchRedeemedCoupons() {
    val userID = UserSingleton.instance.userID
    val db = Firebase.firestore.collection("PointHistory")

    progressBarBranchCouponList.visibility = View.VISIBLE
    db.whereEqualTo("userID", userID)
      .whereEqualTo("redeem", "redeemed")
      .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
        if (firebaseFirestoreException != null) {
          updateStoreListHeaders()
        } else {
          querySnapshot?.documentChanges?.forEach { pointHistory ->
            when(pointHistory.type) {
              DocumentChange.Type.ADDED -> {
                val newRedeemedCoupon = pointHistory.document.toObject(PointHistory::class.java)
                pointHistoryList.add(newRedeemedCoupon)
              }
              DocumentChange.Type.MODIFIED -> {
                val modifiedRedeemedCoupon = pointHistory.document.toObject(PointHistory::class.java)
                val index = pointHistoryList.indexOfFirst {
                  it.couponID == modifiedRedeemedCoupon.couponID
                }
                pointHistoryList[index] = modifiedRedeemedCoupon
              }
              DocumentChange.Type.REMOVED -> {
                val removedRedeemedCoupon = pointHistory.document.toObject(PointHistory::class.java)
                val index = pointHistoryList.indexOfFirst {
                  it.couponID == removedRedeemedCoupon.couponID
                }
                pointHistoryList.removeAt(index)
              }
            } // END: Document change type check
          }  // END: Document loop
        } // END: else check
        updateStoreListHeaders()
        Log.d(TAG, "Point History Count: ${pointHistoryList.map { ph -> ph.couponID }} ")
        progressBarBranchCouponList.visibility = View.GONE
        swipeRefreshCouponList.isRefreshing = false
      } // END: Snapshot listener

//      .get()
//      .addOnSuccessListener { it ->
//        Log.d(TAG, "Point History Count: ${it.count()} ")
//        it.forEach {pointHistory ->
//          val newPointHistory = pointHistory.toObject(PointHistory::class.java)
//          pointHistoryList.add(newPointHistory)
//
//        }
//        updateStoreListHeaders()
//        Log.d(TAG, "Point History Count: ${pointHistoryList.map { ph -> ph.couponID }} ")
//        progressBarBranchCouponList.visibility = View.GONE
//        swipeRefreshCouponList.isRefreshing = false
//      }
//      .addOnFailureListener {
//        Log.w(TAG, it.localizedMessage)
//      }
  }

  private fun updateStoreListHeaders() {

    formattedCouponData.clear()
      this.couponList.map { it ->
        it.selectedStoreName?.forEach { storeName ->
          val currentCoupons = this.storeCoupons[storeName]
          if (currentCoupons != null) {
            // if hashmap value of currentCoupons doesn't have the coupon being parse, add it
            // else just continue
            // to avoid duplicate items
            if (!currentCoupons.map { it -> it.couponID }.contains(it.couponID)) {
              currentCoupons.add(it)
            }
          } else {
            this.storeCoupons[storeName] = arrayListOf(it)
          }
        }
      } // couponList

    if (toCheckBranchID) {
      Log.d(TAG, "Current coupon[${couponList.count()}] $couponList")
      for ((header, coupons) in storeCoupons) {
        Log.d(TAG, "Format $header == ${currentBranch!!.branch}")
        if (header == currentBranch!!.branch) {
//          val newCouponHeader = Coupon(header)
//          val newAdapterItem = AdapterItem(newCouponHeader, AllCouponListAdapter.TYPE_HEADER)
//          formattedCouponData.add(newAdapterItem)
          Log.d(TAG, "SelectedCouponStore[${header}] [${coupons.count()}] \n${coupons.map{it.couponID}}")

          coupons.forEach {coupon ->
            val newCoupon = Coupon(header, coupon.storeID, coupon.couponDetails, coupon.couponID, coupon.couponLabel, coupon.fromDate, coupon.imageURL, coupon.isEnabled, coupon.mainID, coupon.orderBy, coupon.points, coupon.selectedBranch, coupon.selectedStoreName, coupon.untilDate, coupon.fromDateStr, coupon.untilDateStr)
            newCoupon.couponStore = header
            formattedCouponData.add(AdapterItem(newCoupon, AllCouponListAdapter.TYPE_COUPON, false))
          }
        }
      }

    } else {
      for ((header, coupons) in storeCoupons) {
        val newCouponHeader = Coupon(header)
        val newAdapterItem = AdapterItem(newCouponHeader, AllCouponListAdapter.TYPE_HEADER)
        formattedCouponData.add(newAdapterItem)
        Log.d(TAG, "FormatCouponStore[${header}] [${coupons.count()}] \n${coupons.map{it.couponID}}")

        coupons.forEach {coupon ->
          val newCoupon = Coupon(header, coupon.storeID, coupon.couponDetails, coupon.couponID, coupon.couponLabel, coupon.fromDate, coupon.imageURL, coupon.isEnabled, coupon.mainID, coupon.orderBy, coupon.points, coupon.selectedBranch, coupon.selectedStoreName, coupon.untilDate, coupon.fromDateStr, coupon.untilDateStr)
          newCoupon.couponStore = header
          formattedCouponData.add(AdapterItem(newCoupon, AllCouponListAdapter.TYPE_COUPON, false))
        }
      }

    }
    updateData()
    
  }

  override fun onDestroy() {
    super.onDestroy()
    couponListener?.remove()
    redeemedCouponListener?.remove()
  }

  private fun incrementDay(date: Date): Date {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.time = date
    cal.add(Calendar.DATE, 1)
    return cal.time
  }


  companion object {
    const val TAG = "BranchCouponList"
    const val ARG_BRANCH = "branch"
  }

}


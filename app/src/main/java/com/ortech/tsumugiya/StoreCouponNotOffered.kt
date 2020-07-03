package com.ortech.tsumugiya

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.ortech.tsumugiya.Adapters.AllCouponListAdapter
import com.ortech.tsumugiya.Adapters.StoreCouponNotOfferedAdapter
import com.ortech.tsumugiya.Models.*
import com.ortech.tsumugiya.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.fragment_branch_coupon_list.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StoreCouponNotOffered: AppCompatActivity() {

    private lateinit var couponBranchListAdapter: StoreCouponNotOfferedAdapter
    private val db = Firebase.firestore
    private var couponList = ArrayList<Coupon>()
    private var pointHistoryList = ArrayList<PointHistory>()
    private var storeCoupons = HashMap<String, ArrayList<Coupon>>()
    private var formattedCouponData: ArrayList<AdapterItem<Coupon>> = arrayListOf()
    private var currentCoupon: Coupon? = null
    private var couponListener: ListenerRegistration? = null
    private var redeemedCouponListener: ListenerRegistration? = null
    private var toCheckBranchID = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_branch_coupon_list)

        val data = intent.getSerializableExtra(ARG_COUPON)
        if (data != null) {
            currentCoupon = data as Coupon
        }
        setup()
    }

    private fun setup() {
        couponBranchListAdapter =
            StoreCouponNotOfferedAdapter()
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

        if (currentCoupon != null) {
            currentCoupon?.couponStore?.let {couponStore ->
                query = db.collection("CMSCoupon")
                    .whereGreaterThanOrEqualTo("untilDate", Timestamp(Date()))
//          .whereArrayContains("selectedBranch", branchID)
                querySpecificBranchCoupons(query)
                toCheckBranchID = true
            }

        }
    }

    private fun querySpecificBranchCoupons(query: Query) {
        couponListener = query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.e(TAG, "Can't get branch coupons ${firebaseFirestoreException.toString()}")
            } else {

                querySnapshot?.documentChanges?.forEach {documentChange ->
                    val newCoupon: Coupon?
                    when(documentChange.type) {
                        DocumentChange.Type.ADDED -> {
                            newCoupon = documentChange.document.toObject(Coupon::class.java)
                            newCoupon.selectedStoreName?.let {
                                if (!it.contains(currentCoupon?.couponStore ?: "")) {
                                    couponList.add(newCoupon)
                                }
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {
                            newCoupon = documentChange.document.toObject(Coupon::class.java)
                            newCoupon.selectedStoreName?.let {
                                if (it.contains(currentCoupon?.couponStore ?: "")) {
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
                                if (it.contains(currentCoupon?.couponStore ?: "")) {
                                    val index = couponList.indexOfFirst {coupon ->
                                        coupon.couponID == newCoupon.couponID
                                    }
                                    couponList.removeAt(index)
                                }
                            }
                        }
                    } // END: Document type check
                    updateData()
                } // END: Document loop
            } // END: Else
        } // END: Query
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
        const val TAG = "StoreCouponNotOffered"
        const val ARG_COUPON = "coupon"
    }

}

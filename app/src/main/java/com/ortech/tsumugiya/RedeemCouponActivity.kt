package com.ortech.tsumugiya

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.tsumugiya.Helpers.NotificationSender
import com.ortech.tsumugiya.Models.Branch
import com.ortech.tsumugiya.Models.Coupon
import com.ortech.tsumugiya.Models.StaffAccount
import com.ortech.tsumugiya.Models.StaffSingleton
import kotlinx.android.synthetic.main.activity_redeem_coupon.*
import kotlinx.android.synthetic.main.activity_redeem_coupon.toolbarRedeemPoints
import java.text.SimpleDateFormat
import java.util.*

class RedeemCouponActivity : AppCompatActivity() {

  private var redeemData: String? = null
  private var userTotalPointsID: String? = null
  private var currentStaff: StaffAccount? = null
  private val db = Firebase.firestore
  private var branch: Branch? = null

  override fun onResume() {
    super.onResume()
    getCurrentStaff()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_redeem_coupon)

    getCurrentStaff()

    setupToolBar()

    redeemData = intent.getStringExtra(ARGS_REDEEM) as String
    val dataArray = redeemData!!.replace(" ", "").split(",")
    textViewRedeemScannedQRCodeDetails.text = dataArray.joinToString("\n")
    textViewRequiredPoints.text = dataArray[2].toString()

    buttonConfirmRedeem.setOnClickListener {
      confirmRedeem()
    }
  }

  private fun confirmRedeem () {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(R.string.redeem_coupons_customer))
    builder.setMessage(getString(R.string.confirm_submit_details))
    builder.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener(function = { dialog: DialogInterface, _: Int ->
      progressBarRedeemPoints.visibility = View.VISIBLE
      checkBranch()
      dialog.dismiss()
    }))

    builder.setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener(function = { dialog: DialogInterface, _: Int ->
      dialog.dismiss()
    }))

    builder.show()
  }

  private fun checkBranch() {
    val staffBranchID = currentStaff?.currentBranchID ?: return
    val staffBranchName = currentStaff?.branch
    val dataArray = redeemData!!.replace(" ", "").split(",")
    val couponID = dataArray[1]
    //      .whereArrayContains("selectedBranch", staffBranchID)
    db.collection("CMSCoupon")
      .whereEqualTo("couponID", couponID)
      .get()
      .addOnSuccessListener {
        val coupon =  it.documents.first().toObject(Coupon::class.java)
        val selectedBranchArray = coupon?.selectedBranch
        val selectedStoreName = coupon?.selectedStoreName
        var checkedByID = false
        var checkedByName = false

        Log.d(TAG, "staffBranchID=$staffBranchID, branch=$staffBranchName")
        selectedBranchArray?.let { branchNames->
          checkedByID = branchNames.contains(staffBranchID)
       }

        selectedStoreName?.let { storeNames ->
          checkedByName = storeNames.contains(staffBranchName)
        }

        if (checkedByName || checkedByID) {
          checkPoints()
        } else {
          val builder = AlertDialog.Builder(this)
          builder.setTitle(getString(R.string.redeem_coupons_customer))
          builder.setMessage(getString(R.string.invalid))
          builder.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener(function = { dialog: DialogInterface, _: Int ->
            progressBarRedeemPoints.visibility = View.INVISIBLE
            dialog.dismiss()
          }))
          builder.show()
        }

      }
  }

  private fun checkPoints() {
    val totalPointsRef = db.collection("TotalPoints")
    val dataArray = redeemData!!.replace(" ", "").split(",")
    val requiredPoints = dataArray[2].toInt()

    progressBarRedeemPoints.visibility = View.VISIBLE
    totalPointsRef.whereEqualTo("userID", dataArray.first())
      .limit(1)
      .get()
      .addOnSuccessListener {querySnapshot ->
        if (querySnapshot.count() != 0) {
          val currentPoints = querySnapshot.first()["totalPoints"] as Number
          if (currentPoints.toInt() < requiredPoints) {
            progressBarRedeemPoints.visibility = View.INVISIBLE
            invalidPoints()
          } else {
            userTotalPointsID = querySnapshot.first().id
            checkIfRedeemed()
          }
        }
      }
      .addOnFailureListener {
        progressBarRedeemPoints.visibility = View.INVISIBLE
        Toast.makeText(baseContext, "Failed to transfer points", Toast.LENGTH_SHORT).show()
      }

  }

  private fun checkIfRedeemed() {
    redeemData?:return
    val historyRef = db.collection("PointHistory")
    val dataArray = redeemData!!.replace(" ", "").split(",")
    historyRef.whereEqualTo("couponID", dataArray[1])
      .whereEqualTo("userID", dataArray[0])
      .get()
      .addOnSuccessListener {
        if (it.count() != 0) {

          val document = it.first()
          val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
          val today = sdf.format(Date()).toString()
          val timestamp = document["timeStamp"] as Timestamp
          val timestampToday = sdf.format(timestamp.toDate()).toString()

          if (today == timestampToday) {
            progressBarRedeemPoints.visibility = View.INVISIBLE
            alreadyRedeemed()
          } else {
            redeemCoupon()
          }

        } else {
          redeemCoupon()
        }
      }
      .addOnFailureListener {
        progressBarRedeemPoints.visibility = View.INVISIBLE
      }

  }

  private fun redeemCoupon() {
    redeemData?:return
    userTotalPointsID?:return

   val totalPointsRef = db.collection("TotalPoints")
    val dataArray = redeemData!!.replace(" ", "").split(",")
    Log.d(TAG, "Data $dataArray")
    val requiredPoints = -1 * dataArray[2].toDouble()

    totalPointsRef.document(userTotalPointsID!!)
      .set(hashMapOf(
        "totalPoints" to FieldValue.increment(requiredPoints)
      ), SetOptions.merge())
      .addOnSuccessListener {
        addPointHistoryData()
      }
      .addOnFailureListener {
        progressBarRedeemPoints.visibility = View.INVISIBLE
        Toast.makeText(baseContext, "Coupon Redeem Failed", Toast.LENGTH_SHORT).show()
      }
  }

  private fun addPointHistoryData() {
    val pointHistoryRef = db.collection("PointHistory")
    val dataArray = redeemData!!.replace(" ", "").split(",")
    val requiredPoints = dataArray[2].toInt()
    val staffEmail = Firebase.auth.currentUser?.email ?: "error staff email"
    val branchID = currentStaff?.currentBranchID ?: "ajV1krKOREHPusipuEmMQ8hqY8ZKfPLThdbObj1N"

    db.collection("CMSCoupon").whereEqualTo("couponID", dataArray[1])
      .get()
      .addOnSuccessListener {
        if (it.count() != 0) {
          val staffBranch = currentStaff?.branch ?: ""
          val branchURL = branch?.branchURLImages ?: "https://firebasestorage.googleapis.com/v0/b/sakura-dbms.appspot.com/o/branch%2FC2LMuNu1beIqvZWyXwohZDewkdCtLtiTRLqTiCRK?alt=media&token=87bc9e3c-85b4-4178-8a14-5321d12d76a0"
          val pointHistoryData = hashMapOf(
            "userID" to dataArray.first(),
            "transfer" to "",
            "timeStamp" to Timestamp(Date()),
            "storeURL" to "",
            "storeName" to staffBranch,
            "storeID" to "",
            "staffEmail" to staffEmail,
            "redeem" to "redeemed",
            "points" to requiredPoints,
            "mainID" to  "qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP",
            "customerEmail" to "",
            "couponItem" to dataArray[3],
            "couponID" to dataArray[1],
            "branchURL" to branchURL,
            "branchName" to dataArray.last(),
            "branchID" to branchID
          )
          var shouldSendNotification = false

          getUserToken(dataArray.first()) { token ->
            if (token != null) {
              shouldSendNotification = true
            }

            pointHistoryRef.add(pointHistoryData)
              .addOnSuccessListener {
                progressBarRedeemPoints.visibility = View.INVISIBLE

                if (shouldSendNotification) {
                  val title = this.getString(R.string.notification_redeem_title)
                  val appName = this.getString(R.string.app_name)
                  val body = this.getString(R.string.notification_redeem_body, requiredPoints, appName)
                  if (token != null) {
                    NotificationSender.push(token, title, body)
                  }
                }

                clearData()
                val message = getString(R.string.redeem_successful)
                Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
              }
              .addOnFailureListener {
                progressBarRedeemPoints.visibility = View.INVISIBLE
                Toast.makeText(baseContext, "Failed to redeem coupon", Toast.LENGTH_SHORT).show()
              }

          }


        } else {
          progressBarRedeemPoints.visibility = View.INVISIBLE
          Toast.makeText(baseContext, "Failed to redeem coupon", Toast.LENGTH_SHORT).show()
        }
      }

  }

  private fun getUserToken(userID: String, callback: (String?) -> Unit) {

    db.collection("GlobalUsers")
      .whereEqualTo("userID", userID)
      .get()
      .addOnSuccessListener {
        if (!it.isEmpty) {
          val token = it.first().data["token"] as String
          callback(token)
        } else {
          callback(null)
        }
      }
      .addOnFailureListener {
        callback(null)
      }
  }

  private fun getStaffBranchDetails() {
    val branchID = currentStaff?.currentBranchID ?: return

    db.collection("CMSBranches")
      .whereEqualTo("branchID", branchID)
      .get()
      .addOnSuccessListener {
        if (it.count() != 0) {
          this.branch = it.first().toObject(Branch::class.java)
        }
      }
  }

  private fun invalidPoints() {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(R.string.redeem_coupons_customer))
    builder.setMessage(getString(R.string.insufficient_points))
    builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = { dialog: DialogInterface, which: Int ->
      clearData()
      dialog.dismiss()
    }))
    builder.show()
  }

  private fun alreadyRedeemed() {
    Log.d(TAG, "Already redeemed")
    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(R.string.redeem_coupons_customer))
    builder.setMessage(getString(R.string.invalid)) // already redeemed = invalid
    builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = { dialog: DialogInterface, which: Int ->
      clearData()
      dialog.dismiss()
    }))
    builder.show()
  }

  private fun setupToolBar() {
    val toolbar = toolbarRedeemPoints
    toolbar.setNavigationOnClickListener {
      finish()
    }
  }

  private fun clearData() {
    redeemData = null
    textViewRedeemScannedQRCodeDetails.text = ""
    textViewRequiredPoints.text = ""
  }

  private fun getCurrentStaff() {
    currentStaff = StaffSingleton.instance.currentStaff
    getStaffBranchDetails()
  }

  companion object {
    const val TAG = "RedeemCouponActivity"
    const val ARGS_REDEEM = "redeem"
  }

}

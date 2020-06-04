package com.ortech.shopapp

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_redeem_coupon.*
import kotlinx.android.synthetic.main.activity_redeem_coupon.toolbarRedeemPoints
import kotlinx.android.synthetic.main.activity_transfer_points.*
import java.text.SimpleDateFormat
import java.util.*

class RedeemCouponActivity : AppCompatActivity() {

  private var redeemData: String? = null
  private var userTotalPointsID: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_redeem_coupon)

    setupToolBar()

    redeemData = intent.getStringExtra(ARGS_REDEEM) as String
    val dataArray = redeemData!!.replace(" ", "").split(",")
    textViewRedeemScannedQRCodeDetails.text = dataArray.joinToString("\n")
    textViewRequiredPoints.text = dataArray[2].toString()

    buttonConfirmRedeem.setOnClickListener {
      confirmTransfer()
    }
  }

  private fun confirmTransfer () {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Confirm Transfer")
    builder.setMessage("Transfer points")
    builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = { dialog: DialogInterface, _: Int ->
      progressBarRedeemPoints.visibility = View.VISIBLE
      checkPoints()
      dialog.dismiss()
    }))

    builder.setNegativeButton("Cancel", DialogInterface.OnClickListener(function = { dialog: DialogInterface, _: Int ->
      dialog.dismiss()
    }))

    builder.show()
  }

  private fun checkPoints() {
    val db = Firebase.firestore
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
    val db = Firebase.firestore.collection("PointHistory")
    val dataArray = redeemData!!.replace(" ", "").split(",")
    db.whereEqualTo("couponID", dataArray[1])
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

    val db = Firebase.firestore.collection("TotalPoints")
    val dataArray = redeemData!!.replace(" ", "").split(",")
    val requiredPoints = -1 * dataArray[2].toDouble()

    db.document(userTotalPointsID!!)
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
    val db = Firebase.firestore
    val pointHistoryRef = db.collection("PointHistory")
    val dataArray = redeemData!!.replace(" ", "").split(",")
    val requiredPoints = dataArray[2].toInt()
    val staffEmail = Firebase.auth.currentUser?.email ?: "error staff email"

    val pointHistoryData = hashMapOf(
      "userID" to dataArray.first(),
      "transfer" to "",
      "timeStamp" to Timestamp(Date()),
      "storeURL" to "",
      "storeName" to "",
      "storeID" to "",
      "staffEmail" to staffEmail,
      "redeem" to "redeemed",
      "points" to requiredPoints,
      "mainID" to  "qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP",
      "customerEmail" to "",
      "couponItem" to dataArray.last(),
      "couponID" to dataArray[1],
      "branchURL" to "https://firebasestorage.googleapis.com/v0/b/sakura-dbms.appspot.com/o/branch%2FC2LMuNu1beIqvZWyXwohZDewkdCtLtiTRLqTiCRK?alt=media&token=87bc9e3c-85b4-4178-8a14-5321d12d76a0",
      "branchName" to "富山呉羽店",
      "branchID" to "ajV1krKOREHPusipuEmMQ8hqY8ZKfPLThdbObj1N"
    )

    pointHistoryRef.add(pointHistoryData)
      .addOnSuccessListener {
        progressBarRedeemPoints.visibility = View.INVISIBLE
        clearData()
        Toast.makeText(baseContext, "Reduce points by $requiredPoints ", Toast.LENGTH_SHORT).show()
      }
      .addOnFailureListener {
        progressBarRedeemPoints.visibility = View.INVISIBLE
        Toast.makeText(baseContext, "Failed to redeem coupon", Toast.LENGTH_SHORT).show()
      }
  }

  private fun invalidPoints() {
    val builder = AlertDialog.Builder(this)
    builder.setMessage("Insufficient Points")
    builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = { dialog: DialogInterface, which: Int ->
      clearData()
      dialog.dismiss()
    }))
    builder.show()
  }

  private fun alreadyRedeemed() {
    val builder = AlertDialog.Builder(this)
    builder.setMessage("Coupon ALready redeemed by user")
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

  companion object {
    const val TAG = "RedeemCouponActivity"
    const val ARGS_REDEEM = "redeem"
  }
}

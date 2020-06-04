package com.ortech.shopapp

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Models.UserSingleton
import kotlinx.android.synthetic.main.activity_transfer_points.*
import java.text.SimpleDateFormat
import java.util.*

class TransferPointsActivity : AppCompatActivity(), View.OnClickListener {


  private var transferData: String? = null




  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_transfer_points)

    transferData = intent.getStringExtra(ARGS_TRANSFER) as String

    if (transferData != null) {
      textViewScannedQRCodeDetails.text = transferData!!.split(",").toString()
    }

    setupToolBar()
    buttonIncrementPoints.setOnClickListener(this)
    buttonDecrementPoints.setOnClickListener(this)
    buttonConfirmTransfer.setOnClickListener(this)
  }

  override fun onClick(v: View?) {
    v?.let {
      when(it.id) {
        R.id.buttonIncrementPoints -> {
          val currentPointsStr = editTextTransferPoints.text.toString()
          var currentPoints = 0
          if (currentPointsStr != "") {
            currentPoints = currentPointsStr.toInt()
          }

          editTextTransferPoints.text = Editable.Factory.getInstance().newEditable("${currentPoints+1}")
          editTextTransferPoints.setSelection(editTextTransferPoints.text.length)
        }
        R.id.buttonDecrementPoints -> {
          val currentPoints = editTextTransferPoints.text.toString().toInt()
          if (currentPoints != 0) {
            editTextTransferPoints.text = Editable.Factory.getInstance().newEditable("${currentPoints-1}")
          }
          editTextTransferPoints.setSelection(editTextTransferPoints.text.length)
        }
        R.id.buttonConfirmTransfer -> {
          if (transferData == null) {
            rescanMessage()
          } else {
            confirmTransfer()
          }
        }
      }
    } // END : Null check
  }

  private fun confirmTransfer () {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Confirm Transfer")
    builder.setMessage("Transfer points")
    builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = {dialog: DialogInterface, which: Int ->
      progressBarTransferPoints.visibility = View.VISIBLE
      transferPoints()
      dialog.dismiss()
    }))

    builder.setNegativeButton("Cancel", DialogInterface.OnClickListener(function = {dialog: DialogInterface, which: Int ->
      dialog.dismiss()
    }))

    builder.show()
  }

  private fun rescanMessage() {
    val builder = AlertDialog.Builder(this)
    builder.setMessage("Re scan qr code")
    builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = {dialog: DialogInterface, which: Int ->
      dialog.dismiss()
    }))
    builder.show()
  }

  private fun transferPoints() {
    transferData?:return
    val db = Firebase.firestore
    val totalPointsRef = db.collection("TotalPoints")
    val userData = transferData!!.replace(" ","").split(",")
    val toTransferPoints = editTextTransferPoints.text.toString().toDouble()


    totalPointsRef.whereEqualTo("userID", userData.first())
      .limit(1)
      .get()
      .addOnSuccessListener {querySnapshot ->
        if (querySnapshot.count() != 0) {
          val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
          val today = sdf.format(Date()).toString()
          val timestamp = querySnapshot.first()["dates"] as Timestamp
          val timestampToday = sdf.format(timestamp.toDate()).toString()
          val totalPointData: HashMap<String, Any>
          if (today == timestampToday) {
            totalPointData = hashMapOf(
              "totalPoints" to FieldValue.increment(toTransferPoints),
              "lastPoints" to toTransferPoints,
              "pointsToday" to FieldValue.increment(toTransferPoints)
            )
          } else {
            totalPointData = hashMapOf(
              "totalPoints" to FieldValue.increment(toTransferPoints),
              "lastPoints" to toTransferPoints,
              "pointsToday" to toTransferPoints,
              "dates" to Timestamp(Date())
            )
          }
          totalPointsRef.document(querySnapshot.first().id)
            .set(totalPointData, SetOptions.merge())
            .addOnSuccessListener {
              // Add PointHistory
              addPointHistoryData()
            }
        }
      }
      .addOnFailureListener {
        progressBarTransferPoints.visibility = View.INVISIBLE
        Toast.makeText(baseContext, "Failed to transfer points", Toast.LENGTH_SHORT).show()
      }

  }

  private fun addPointHistoryData() {
    val db = Firebase.firestore
    val pointHistoryRef = db.collection("PointHistory")
    val userData = transferData!!.replace(" ","").split(",")
    val toTransferPoints = editTextTransferPoints.text.toString().toDouble()
    val staffEmail = Firebase.auth.currentUser?.email ?: "error staff email"
    val pointHistoryData = hashMapOf(
      "userID" to userData.first(),
      "transfer" to "transferred",
      "timeStamp" to Timestamp(Date()),
      "storeURL" to "",
      "storeName" to "",
      "storeID" to "",
      "staffEmail" to staffEmail,
      "redeem" to "",
      "points" to toTransferPoints.toInt(),
      "mainID" to  "qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP",
      "customerEmail" to "",
      "couponItem" to "",
      "couponID" to "",
      "branchURL" to "https://firebasestorage.googleapis.com/v0/b/sakura-dbms.appspot.com/o/branch%2FC2LMuNu1beIqvZWyXwohZDewkdCtLtiTRLqTiCRK?alt=media&token=87bc9e3c-85b4-4178-8a14-5321d12d76a0",
      "branchName" to "富山呉羽店",
      "branchID" to "ajV1krKOREHPusipuEmMQ8hqY8ZKfPLThdbObj1N"
      )

    pointHistoryRef.add(pointHistoryData)
      .addOnSuccessListener {
        progressBarTransferPoints.visibility = View.INVISIBLE
        clearData()
        Toast.makeText(baseContext, "Transferred ${toTransferPoints.toInt()}", Toast.LENGTH_SHORT).show()
      }
      .addOnFailureListener {
        progressBarTransferPoints.visibility = View.INVISIBLE
        Toast.makeText(baseContext, "Failed to Transfer ${toTransferPoints.toInt()}", Toast.LENGTH_SHORT).show()
      }
  }

  private fun clearData() {
    editTextTransferPoints.text.clear()
    textViewScannedQRCodeDetails.text = ""
    transferData = null
  }

  private fun setupToolBar() {
    val toolbar = toolbarRedeemPoints
    toolbar.setNavigationOnClickListener {
      finish()
    }
  }

  companion object {
    const val TAG = "TransferPointsActivity"
    const val ARGS_TRANSFER = "transfer"
  }

}

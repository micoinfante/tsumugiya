package com.ortech.shopapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.ortech.shopapp.Helpers.NotificationSender
import com.ortech.shopapp.Models.*
import github.nisrulz.qreader.QRDataListener
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.activity_qr_code_scanner.*
import kotlinx.android.synthetic.main.activity_redeem_coupon.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class QRCodeScannerActivity : AppCompatActivity() {

  private var qrScanIntegrator: IntentIntegrator? = null
  private var qrEader: QREader? = null
  private var userType: String? = TYPE_STAFF
  private var hasScanned = false

  override fun onStart() {
    super.onStart()
    qrEader?.start()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_qr_code_scanner)

    val surfaceView = camera_view
    qrEader = QREader.Builder(this.baseContext, surfaceView,  QRDataListener {data ->
      //      transferPoints(data)

      if (!hasScanned) {
        checkTransaction(data)
        Log.d(TAG, "Scanned data: $data")


      }
//      qrEader?.stop()


    }).facing(QREader.BACK_CAM)
      .enableAutofocus(true)
      .height(500)
      .width(camera_view.width)
      .build()
    if (!this.qrEader?.isCameraRunning!!) {
      qrEader?.start()
    }

    surfaceView.setOnClickListener {
      performAction()
    }

    setupToolBar()

    // check what type is using: User or staff
    userType = intent.getStringExtra(ARGS_TYPE)
    Log.d(TAG, "Using by type: $userType")
  }


  private fun setupToolBar() {
    val toolbar = toolbarHistory
    toolbar.setNavigationOnClickListener {
      closeActivity()
    }
  }

  private fun closeActivity() {
    if (userType == TYPE_CUSTOMER) {
      if (supportFragmentManager.backStackEntryCount == 0) {
        this.finish();
      } else {
        super.onBackPressed(); //replaced
      }
    } else {
      Firebase.auth.signOut()
      val intent = Intent(baseContext, MainActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      startActivity(intent)
    }
  }

  private fun performAction() {
    qrScanIntegrator?.initiateScan()
  }

  override fun onResume() {
    super.onResume()


    qrEader?.initAndStart(camera_view)
  }

  override fun onPause() {
    super.onPause()
    qrEader?.releaseAndCleanup()
  }


  private fun transferPoints(data: String) {

  }

  private fun checkTransaction(data: String) {
    if (userType == TYPE_CUSTOMER) {
        hasScanned = true
        transferPointsByCustomer(data)
    } else {
      processQRCodeOfCustomer(data)
    }
  }

  private fun processQRCodeOfCustomer(data: String) {
    if (data.contains("transfer")) {
      transferPointsToCustomer(data)
    } else {
      redeemCouponOfCustomer(data)
    }
  }

  override fun onBackPressed() {
    super.onBackPressed()
    finish()
  }

  private fun transferPointsToCustomer(data: String) {
    val intent = Intent(baseContext, TransferPointsActivity::class.java)
    intent.putExtra(TransferPointsActivity.ARGS_TRANSFER, data)
    startActivity(intent)
  }

  private fun transferPointsByCustomer(data: String) {
    runOnUiThread {
      progressbarQRScanner.visibility = View.VISIBLE
    }
//    qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP,2020-06-30,
//    tDiF8oBOe9iGVueS0iN7JZ5yrgEBib1CovFm1Fu4,二代目らーめん世界   ,
//    32,https://apps.apple.com/us/app/%E3%83%A9%E3%83%BC%E3%83%A1%E3%83%B3%E4%B8%96%E7%95%8C/id1503125317?ls=1
    Log.d(TAG, "Transferring point customer")
    val dataArray = data.replace(" ", "").split(",")
    val expirationDate = dataArray[1]

    val expiry = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(expirationDate)
    expiry?.let {
      if (it < Date()) {
        expiredQRCodeMessage()
        runOnUiThread {
          progressbarQRScanner.visibility = View.INVISIBLE
        }
        return
      }
    }

    checkIfAlreadyRedeemed(data)

  }

  private fun addTotalPointHistory(data: String) {
    val dataArray = data.replace(" ", "").split(",")
    val points = dataArray[4].toDouble()
    val db = Firebase.firestore.collection("TotalPoints")

    db.whereEqualTo("userID", UserSingleton.instance.userID)
      .get()
      .addOnSuccessListener { querySnapshot ->
        if (querySnapshot.count() != 0) {
          val document = querySnapshot.first()
          val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
          val today = sdf.format(Date()).toString()
          val timestamp = document["dates"] as Timestamp
          val timestampToday = sdf.format(timestamp.toDate()).toString()
          var totalPoints: HashMap<String, Any>

          totalPoints = if (today == timestampToday) {
            hashMapOf(
              "totalPoints" to FieldValue.increment(points),
              "lastPoints" to points,
              "pointsToday" to FieldValue.increment(points)
            )
          } else {
            hashMapOf(
              "dates" to Timestamp(Date()),
              "totalPoints" to FieldValue.increment(points),
              "lastPoints" to points,
              "pointsToday" to points
            )
          }

          db.document(document.id)
            .set(totalPoints, SetOptions.merge())
            .addOnCompleteListener {
              if (it.isComplete || it.isSuccessful) {
                getBranchDetails(data)

              }
            }
            .addOnFailureListener {
              progressbarQRScanner.visibility = View.INVISIBLE
              closeActivity()
            }

        }
      }
      .addOnFailureListener {
        Log.d(TAG, "Failed to transfer points to ${UserSingleton.instance.userID} - ${it.localizedMessage}")
      }

  }

  private fun checkIfAlreadyRedeemed(data: String) {
    val db = Firebase.firestore
    val pointHistoryRef = db.collection("PointHistory")
    val dataArray = data.replace(" ", "").split(",")
    val mainID = dataArray[0]
    val expirationDate = dataArray[1]
    val branchID = dataArray[2]
    val storeName = dataArray[3]
    val points = dataArray[4].toDouble()

    pointHistoryRef
      .whereEqualTo("userID", UserSingleton.instance.userID)
      .whereEqualTo("storeName", expirationDate)
      .whereEqualTo("branchID", branchID)
      .orderBy("timeStamp", Query.Direction.DESCENDING)
      .limit(1)
      .get()
      .addOnSuccessListener {
        if (it.isEmpty) {
//          Log.d(TAG, "D1CFA807-DE57-43F3-9254-794415DF1E08 Not Yet redeemed")
          addTotalPointHistory(data)
        } else {
//          Log.d(TAG, "D1CFA807-DE57-43F3-9254-794415DF1E08 Already redeemed")
          val pointLog = it.first().toObject(PointHistory::class.java)
          val timestamp = pointLog.timeStamp ?: Timestamp(Date())
          val availability = incrementDate(timestamp.toDate())

          val check = Date(availability.time)

          if (check > Date()) {
            alreadyRedeemedMessage()
            progressbarQRScanner.visibility = View.INVISIBLE
          } else {
            addTotalPointHistory(data)

          }
        }
      }
      .addOnFailureListener {
        progressbarQRScanner.visibility = View.INVISIBLE
        Log.d(TAG, it.localizedMessage)
      }

  }

  // When customer scans a QRCode add a log
  // this will be use for checking if the customer scanned the qrcode already
  private fun addPointHistoryOfCustomer(data: String, branchURL: String?) {
    //    qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP,2020-06-30,
//    tDiF8oBOe9iGVueS0iN7JZ5yrgEBib1CovFm1Fu4,二代目らーめん世界   ,
//    32,https://apps.apple.com/us/app/%E3%83%A9%E3%83%BC%E3%83%A1%E3%83%B3%E4%B8%96%E7%95%8C/id1503125317?ls=1

    val dataArray = data.replace(" ", "").split(",")
    val mainID = dataArray[0]
    val expirationDate = dataArray[1]
    val branchID = dataArray[2]
    val branchName = dataArray[3]
    val points = dataArray[4].toDouble()

    val db = Firebase.firestore.collection("PointHistory")

    val imageURL = branchURL ?: "https://firebasestorage.googleapis.com/v0/b/sakura-dbms.appspot.com/o/branch%2FC2LMuNu1beIqvZWyXwohZDewkdCtLtiTRLqTiCRK?alt=media&token=87bc9e3c-85b4-4178-8a14-5321d12d76a0"
    val pointHistoryData = hashMapOf(
      "userID" to UserSingleton.instance.userID,
      "timeStamp" to Timestamp(Date()),
      "storeURL" to imageURL,
      "storeName" to expirationDate,
      "storeID" to mainID,
            "staffEmail" to "",
            "transfer" to "transferred",
            "points" to points,
            "mainID" to mainID,
            "customerEmail" to "",
            "branchURL" to imageURL,
            "branchName" to branchName,
            "branchID" to branchID
          )

    db.add(pointHistoryData).addOnSuccessListener {
        hasScanned = false
      progressbarQRScanner.visibility = View.INVISIBLE
      val title = this.getString(R.string.notification_transfer_title)
      val appName = this.getString(R.string.app_name)
      val body = this.getString(R.string.notification_transfer_body, points.toInt(), appName)
       NotificationSender.push(UserSingleton.instance.fcmToken, title, body)
      closeActivity()
    }
      .addOnFailureListener {
        progressbarQRScanner.visibility = View.INVISIBLE
        Toast.makeText(baseContext, "Failed to transfer points", Toast.LENGTH_SHORT).show()
      }


  }

  private fun getBranchDetails(data: String) {
    val dataArray = data.replace(" ", "").split(",")
    val mainID = dataArray[0]
    val expirationDate = dataArray[1]
    val branchID = dataArray[2]
    val branchName = dataArray[3]
    val points = dataArray[4].toDouble()

    val db = Firebase.firestore

    db.collection("CMSBranches")
      .whereEqualTo("branchID", branchID)
      .get()
      .addOnSuccessListener {
        if (it.count() != 0) {
          val branch = it.first().toObject(Branch::class.java)
          val imageURL = branch.branchURLImages
          addPointHistoryOfCustomer(data, imageURL)
        }
      }
      .addOnFailureListener {
        progressbarQRScanner.visibility = View.INVISIBLE
      }
  }

  private fun redeemCouponOfCustomer(data: String) {
    val intent = Intent(baseContext, RedeemCouponActivity::class.java)
    intent.putExtra(RedeemCouponActivity.ARGS_REDEEM, data)
    startActivity(intent)
  }

  private fun expiredQRCodeMessage() {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(R.string.transfer))
    builder.setMessage("ポイントを獲得するには24時間お待ちください")
    builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = { dialog: DialogInterface, which: Int ->
      hasScanned = false
      dialog.dismiss()
    }))
    builder.show()
  }

  private fun alreadyRedeemedMessage() {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(R.string.transfer))
    builder.setMessage(getString(R.string.invalid)) // already redeemed = invalid
    builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = { dialog: DialogInterface, which: Int ->
      hasScanned = false
      dialog.dismiss()
    }))
    builder.show()
  }

  private fun incrementDate(date: Date): Date {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.time = date
    cal.add(Calendar.SECOND, CMSSettings.instance.currentQRCodeTimeLimit().toInt())
    return cal.time
  }

  companion object {
    const val TAG = "QRCodeScannerActivity"
    const val TYPE_STAFF = "STAFF"
    const val TYPE_CUSTOMER = "CUSTOMER"
    const val ARGS_TYPE = "ARGS_TYPE"
  }


}

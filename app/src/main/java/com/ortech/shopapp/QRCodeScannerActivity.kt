package com.ortech.shopapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.ortech.shopapp.Models.PointHistory
import com.ortech.shopapp.Models.TotalPoints
import com.ortech.shopapp.Models.UserSingleton
import github.nisrulz.qreader.QRDataListener
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.activity_qr_code_scanner.*
import java.text.SimpleDateFormat
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
      if (data.split(",").size > 4) {
        hasScanned = true
        transferPointsByCustomer(data)
      }
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
//    val dataArray = data.replace(" ", "").split(",")
//    Log.d(TAG, "Trying to transfer points $dataArray.toString()")
//    progressbarQRScanner.visibility = View.VISIBLE
//    val pointHistory = PointHistory(
//      "ajV1krKOREHPusipuEmMQ8hqY8ZKfPLThdbObj1N","富山呉羽店",
//      "https://firebasestorage.googleapis.com/v0/b/sakura-dbms.appspot.com/o/branch%2FC2LMuNu1beIqvZWyXwohZDewkdCtLtiTRLqTiCRK?alt=media&token=87bc9e3c-85b4-4178-8a14-5321d12d76a0",
//      "","","","qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP",
//      100, "","rhuet.transit@gmail.com",
//      "","","", Timestamp(Date()),"transferred",
//      dataArray.first()
//    )
//
//
//
//    val totalPoints = TotalPoints(Timestamp(Date()),
//       UserSingleton.instance.toString(),
//      100, 100, "qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP",
//      "ラーメン世界", UserSingleton.instance.getCurrentPoints()+100,
//        UserSingleton.instance.userID
//      )
//
//
//    val db = Firebase.firestore
//
//    db.collection("PointHistory").add(pointHistory)
//      .addOnCompleteListener {
//        if (it.isSuccessful) {
//          Log.d(TAG, "Transferred 100points to ${UserSingleton.instance.userID}")
//        }
//        if (it.isComplete) {
//          progressbarQRScanner.visibility = View.INVISIBLE
//          closeActivity()
//        }
//      }
//      .addOnFailureListener {
//        Log.d(TAG, "Failed to transfer points to ${UserSingleton.instance.userID} - ${it.localizedMessage}")
//      }
  }

  private fun transferPointsByCustomer(data: String) {
    val dataArray = data.replace(" ", "").split(",")
    val branchID = dataArray[0]
    val storeName = dataArray[1]
    val couponID: String = dataArray[2]
    val branchName: String = dataArray[3]
    val points = dataArray[4].toDouble()

    Log.d(TAG, "Trying to transfer points $dataArray.toString()")


//    val pointHistory = PointHistory(
//      "ajV1krKOREHPusipuEmMQ8hqY8ZKfPLThdbObj1N",branchName,
//      "https://firebasestorage.googleapis.com/v0/b/sakura-dbms.appspot.com/o/branch%2FC2LMuNu1beIqvZWyXwohZDewkdCtLtiTRLqTiCRK?alt=media&token=87bc9e3c-85b4-4178-8a14-5321d12d76a0",
//      "","","","qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP",
//      point, "","rhuet.transit@gmail.com",
//      "",storeName,"", Timestamp(Date()),"transferred",
//      dataArray.first()
//    )



//    val totalPoints = TotalPoints(Timestamp(Date()),
//      UserSingleton.instance.toString(),
//      100, 100, "qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP",
//      storeName, FieldValue.increment(points),
//      UserSingleton.instance.userID
//    )

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
                runOnUiThread {
                  val message = getString(R.string.earn_points_successful, points.toString())
                  Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show()
                }
                Handler().post(Runnable {
                  progressbarQRScanner.visibility = View.INVISIBLE
                  //do your stuff here
                  this.finish()
                })

//                runOnUiThread (
//                  Runnable(){
//                    Log.d(TAG, "Successfully transferred $points")
//                    this@QRCodeScannerActivity.finish();
//                  }
//                )
              }
            }
            .addOnFailureListener {
              Toast.makeText(baseContext, "Failed to transfer points", Toast.LENGTH_SHORT).show()
              progressbarQRScanner.visibility = View.INVISIBLE
              closeActivity()
            }

        }
      }
      .addOnFailureListener {
        Log.d(TAG, "Failed to transfer points to ${UserSingleton.instance.userID} - ${it.localizedMessage}")
      }


  }

  private fun redeemCouponOfCustomer(data: String) {
    val intent = Intent(baseContext, RedeemCouponActivity::class.java)
    intent.putExtra(RedeemCouponActivity.ARGS_REDEEM, data)
    startActivity(intent)
  }

  private fun processQRCodeByStaff(data: String) {

  }

  companion object {
    const val TAG = "QRCodeScannerActivity"
    const val TYPE_STAFF = "STAFF"
    const val TYPE_CUSTOMER = "CUSTOMER"
    const val ARGS_TYPE = "ARGS_TYPE"
  }


}

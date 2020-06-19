package com.ortech.shopapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.ortech.shopapp.Models.RequestCode
import com.ortech.shopapp.Models.UserSingleton
import kotlinx.android.synthetic.main.activity_customer_qr_code.*
import java.text.SimpleDateFormat
import java.util.*

class CustomerQRCodeActivity : Fragment(){

  private var handler: Handler? = null
  private var runnable: Runnable? = null


  override fun onResume() {
    super.onResume()
    Log.d(TAG, "onResume")
    startHandler()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getTotalPoints()
    Log.d(TAG, "onCreate")
    startHandler()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    Log.d(TAG, "onCreateView")
    return inflater.inflate(R.layout.activity_customer_qr_code, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.d(TAG, "onViewCreated")
    generateQRCode()
    setupScanner()
    setupCameraPermission()

//    Log.d(TAG, "${view.isInTouchMode}")

    customerQRCodeContainer.setOnTouchListener(object: View.OnTouchListener {
      override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        if (p1?.action == MotionEvent.ACTION_MOVE) {

          Log.d(TAG, "move")
          stopHandler()
          startHandler()
        }
        return true
      }
    })

  }

  private fun stopHandler() {
    runnable?.let { handler?.removeCallbacks(it) }
  }

  private fun startHandler() {
    if (runnable == null) {
     runnable =  Runnable {
        val parentActivity = activity as BottomNavigationActivity
        parentActivity.navView.selectedItemId = R.id.navigation_home
      }
    }
    if (handler == null) {
      handler = Handler(Looper.myLooper()!!)
    }
    handler?.postDelayed(runnable!!, 8000)
  }

  private fun generateQRCode() {
    val imageView = imageViewQRCode
    val content = UserSingleton.instance.userID + ", transfer"

    val multiFormatWriter = MultiFormatWriter()
    val hintMap = mapOf(EncodeHintType.MARGIN to 0)
    val bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512, hintMap)
    val qrCode = BarcodeEncoder().createBitmap(bitMatrix)
    Glide.with(this.view!!).load(qrCode).into(imageView)
  }

  private fun setupScanner() {
    btnScanQRCode.setOnClickListener {
      val intent = Intent(activity, QRCodeScannerActivity::class.java)
      intent.putExtra(QRCodeScannerActivity.ARGS_TYPE, QRCodeScannerActivity.TYPE_CUSTOMER)
      startActivity(intent)
    }
  }

  private fun setupCameraPermission() {
    val permission =
      this.context?.let { it1 ->
        ContextCompat.checkSelfPermission(it1, Manifest.permission.CAMERA) }


    if (permission != PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Permission to use camera is denied")
      makeRequest()
    }

  }

  private fun makeRequest() {
    this.context?.let {
      ActivityCompat.requestPermissions(
        activity as AppCompatActivity,
        arrayOf(Manifest.permission.CAMERA),
        RequestCode.CAMERA)
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
        RequestCode.CAMERA -> {

        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
          Log.i(TAG, "Permission has been denied by user")
        } else {
          Log.i(TAG, "Permission has been granted by user")
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d(TAG, "onDestroy")
    stopHandler()
  }

  override fun onPause() {
    super.onPause()
    Log.d(TAG, "onPause")
    stopHandler()
  }

  private fun getTotalPoints() {
    val userID = UserSingleton.instance.userID
    val db = Firebase.firestore

    db.collection("TotalPoints").whereEqualTo("userID", userID)
      .limit(1)
      .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
        if (firebaseFirestoreException != null) {
          Log.w(TAG, "Listener Failed", firebaseFirestoreException)
        }

        if (querySnapshot != null) {
          val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
          val today = sdf.format(Date()).toString()
          val timestamp = querySnapshot.first()["dates"] as Timestamp
          val timestampToday = sdf.format(timestamp.toDate()).toString()
          val currentPoints = querySnapshot.first()["totalPoints"] as Number

          textViewCustomerCurrentPoints.text = currentPoints.toInt().toString()

          if (today == timestampToday) {
            val pointsToday = querySnapshot.first()["pointsToday"] as Number
            val lastPoints = querySnapshot.first()["lastPoints"] as Number
            textViewCustomerLastPoints.text = lastPoints.toInt().toString()
            textViewCustomerEarnedPoints.text = pointsToday.toInt().toString()
          } else {
            textViewCustomerLastPoints.text = "0"
            textViewCustomerEarnedPoints.text = "0"
          }

        }
      }

  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    Log.d(TAG, "onAttached")
  }



  companion object {
    const val TAG = "CustomerQRCode"
  }

}

interface UserInteractionListener {
  fun onUserInteraction()
}

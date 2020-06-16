package com.ortech.shopapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.encoder.QRCode
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.ortech.shopapp.Helpers.NotificationSender
import com.ortech.shopapp.Models.RequestCode
import com.ortech.shopapp.Models.UserSingleton
import kotlinx.android.synthetic.main.activity_customer_qr_code.*
import java.text.SimpleDateFormat
import java.util.*

class CustomerQRCodeActivity : Fragment(){

  private var handler = Handler()
  private var runnable = Runnable { Log.d(TAG, "Add action to back") }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getTotalPoints()
    startHandler()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.activity_customer_qr_code, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    generateQRCode()
    setupScanner()
    setupCameraPermission()

    view.setOnTouchListener { v, event ->
      when (event?.action) {
        MotionEvent.ACTION_MOVE -> {
          stopHandler()
          startHandler()
        }
      }

      v?.onTouchEvent(event) ?: true
    }
  }

  private fun stopHandler() {
    handler.removeCallbacks(runnable)
  }

  private fun startHandler() {
    handler.postDelayed(runnable, 10000)
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
//      val intent = Intent(activity, QRCodeScannerActivity::class.java)
//      intent.putExtra(QRCodeScannerActivity.ARGS_TYPE, QRCodeScannerActivity.TYPE_CUSTOMER)
//      startActivity(intent)
      NotificationSender.push()
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

  private fun getUUID() : String? {
    val sharedPreferences = activity?.getSharedPreferences(getString(R.string.preference_UUID_key),
      Context.MODE_PRIVATE) ?: return null
    val currentUUID = sharedPreferences.getString(getString(R.string.preference_UUID_key), null)
    Log.d(TAG, "current Stored UUID: $currentUUID")
    return if (currentUUID.isNullOrEmpty()) {
      null
    } else {
      currentUUID
    }
  }


  companion object {
    const val TAG = "CustomerQRCode"
  }


}

interface UserInteractionListener {
  fun onUserInteraction()
}

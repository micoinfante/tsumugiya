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
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.ortech.shopapp.Models.RequestCode
import kotlinx.android.synthetic.main.activity_customer_qr_code.*

class CustomerQRCodeActivity : Fragment(){

  private var handler = Handler()
  private var runnable = Runnable { Log.d(TAG, "Add action to back") }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
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
    val content = getUUID() + ", transfer"

    val multiFormatWriter = MultiFormatWriter()
    val hintMap = mapOf(EncodeHintType.MARGIN to 0)
    val bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512, hintMap)
    val qrCode = BarcodeEncoder().createBitmap(bitMatrix)
    Glide.with(this.view!!).load(qrCode).into(imageView)
  }

  private fun setupScanner() {
    btnScanQRCode.setOnClickListener {
      val intent = Intent(activity, QRCodeScannerActivity::class.java)
      startActivity(intent)
//      val activity = (context as? AppCompatActivity)
//      val fragment = QRCodeScannerActivity()
//      val transaction = activity?.supportFragmentManager?.beginTransaction()
//
//      transaction?.setCustomAnimations(
//        R.anim.enter_from_left,
//        R.anim.exit_to_left,
//        R.anim.enter_from_left,
//        R.anim.exit_to_left
//      )
//      transaction?.replace(R.id.container, fragment,"CustomerQRCode")
//      transaction?.addToBackStack(null)
//      transaction?.commit()

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

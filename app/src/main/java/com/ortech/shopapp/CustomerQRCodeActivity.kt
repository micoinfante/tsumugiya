package com.ortech.shopapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_customer_qr_code.*
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.journeyapps.barcodescanner.CaptureManager
import com.ortech.shopapp.Models.RequestCode

class CustomerQRCodeActivity : Fragment() {

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

  }

  private fun generateQRCode() {
    val imageView = view?.findViewById<ImageView>(R.id.imageViewQRCode)
    val content = getUUID() + ", transfer"

    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
      for (y in 0 until height) {
        bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
      }
    }
    imageView?.setImageBitmap(bitmap)
  }

  private fun setupScanner() {
    btnScanQRCode.setOnClickListener {
//      val intent = Intent(activity, QRCodeScannerActivity::class.java)
//      startActivity(intent)
      val activity = (context as? AppCompatActivity)
      val fragment = QRCodeScannerActivity()
      val transaction = activity?.supportFragmentManager?.beginTransaction()

      transaction?.setCustomAnimations(
        R.anim.enter_from_left,
        R.anim.exit_to_left,
        R.anim.enter_from_left,
        R.anim.exit_to_left
      )
      transaction?.replace(R.id.container, fragment,"CustomerQRCode")
      transaction?.addToBackStack(null)
      transaction?.commit()

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

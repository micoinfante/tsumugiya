package com.ortech.shopapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_customer_qr_code.*
import java.util.*

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
    generateQRCode("testString")
  }

  private fun generateQRCode(data: String) {
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

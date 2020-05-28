package com.ortech.shopapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.zxing.integration.android.IntentIntegrator
import github.nisrulz.qreader.QRDataListener
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.activity_qr_code_scanner.*
import org.json.JSONException


class QRCodeScannerActivity : Fragment() {

  private var qrScanIntegrator: IntentIntegrator? = null
  private var qrEader: QREader? = null

  override fun onStart() {
    super.onStart()
    qrEader?.start()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.activity_qr_code_scanner, container, false)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//    nav_view.visibility = View.GONE
    val activity = (context as? AppCompatActivity)
    val navBar = activity?.findViewById<NavigationView>(R.id.nav_view)
  }

  override fun onDestroy() {
    super.onDestroy()
//    nav_view.visibility = View.VISIBLE
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val surfaceView = camera_view
    qrEader = QREader.Builder(this.context, surfaceView,  QRDataListener {data ->
      //      Toast.makeText(this, data, Toast.LENGTH_LONG).show()
    }).facing(QREader.BACK_CAM)
      .enableAutofocus(true)
      .height(camera_view.height)
      .width(camera_view.width)
      .build()
    if (!this.qrEader?.isCameraRunning!!) {
      qrEader?.start()
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

//  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//    val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//    if (result != null) {
//      // If QRCode has no data.
//      if (result.contents == null) {
//        Toast.makeText(this, "Null data", Toast.LENGTH_LONG).show()
//      } else {
//        // If QRCode contains data.
//        try {
//          // Converting the data to json format
//          Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
//
//        } catch (e: JSONException) {
//          e.printStackTrace()
//
//          // Data not in the expected format. So, whole object as toast message.
//          Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
//        }
//
//      }
//    } else {
//      super.onActivityResult(requestCode, resultCode, data)
//    }
//  }


}

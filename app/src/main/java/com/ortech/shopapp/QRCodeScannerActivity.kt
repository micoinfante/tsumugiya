package com.ortech.shopapp

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.ortech.shopapp.Models.PointHistory
import com.ortech.shopapp.Models.UserSingleton
import github.nisrulz.qreader.QRDataListener
import github.nisrulz.qreader.QREader
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.activity_qr_code_scanner.*
import kotlinx.android.synthetic.main.fragment_branch_coupon_list.*
import org.json.JSONException
import java.util.*


class QRCodeScannerActivity : AppCompatActivity() {

  private var qrScanIntegrator: IntentIntegrator? = null
  private var qrEader: QREader? = null

  override fun onStart() {
    super.onStart()
    qrEader?.start()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_qr_code_scanner)

    val surfaceView = camera_view
    qrEader = QREader.Builder(this.baseContext, surfaceView,  QRDataListener {data ->
      transferPoints(data)
      Log.d(TAG, "Scanned data: $data")
      qrEader?.stop()
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

  }


  private fun setupToolBar() {
    val toolbar = toolbar
    toolbar.setNavigationOnClickListener {
      closeActivity()
    }
  }

  private fun closeActivity() {
    if (supportFragmentManager.backStackEntryCount == 0) {
      this.finish();
    } else {
      super.onBackPressed(); //replaced
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
    Log.d(TAG, "Trying to transfer points")
    val dataArray = data.split(",")
    progressbarQRScanner.visibility = View.VISIBLE
    val transferData = PointHistory(
      "ajV1krKOREHPusipuEmMQ8hqY8ZKfPLThdbObj1N","富山呉羽店","https://firebasestorage.googleapis.com/v0/b/sakura-dbms.appspot.com/o/branch%2FC2LMuNu1beIqvZWyXwohZDewkdCtLtiTRLqTiCRK?alt=media&token=87bc9e3c-85b4-4178-8a14-5321d12d76a0",
      "","","","qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP",
      100, "","rhuet.transit@gmail.com",
      "","","", Date(),"transferred",
      dataArray.first()
    )
    val db = Firebase.firestore

    db.collection("PointHistory").add(transferData)
      .addOnCompleteListener {
        if (it.isSuccessful) {
          Log.d(TAG, "Transferred 100points to ${UserSingleton.instance.userID}")
        }
        if (it.isComplete) {
          progressbarQRScanner.visibility = View.INVISIBLE
          closeActivity()
        }
      }
      .addOnFailureListener {
        Log.d(TAG, "Failed to transfer points to ${UserSingleton.instance.userID} - ${it.localizedMessage}")
      }
  }

  companion object {
    const val TAG = "QRCodeScannerActivity"
  }


}

package com.ortech.shopapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException

class QRCodeScannerActivity : AppCompatActivity() {

  private var qrScanIntegrator: IntentIntegrator? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_qr_code_scanner)

    qrScanIntegrator = IntentIntegrator(this)

  }

  private fun performAction() {
    qrScanIntegrator?.initiateScan()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
    if (result != null) {
      // If QRCode has no data.
      if (result.contents == null) {
        Toast.makeText(this, "Null data", Toast.LENGTH_LONG).show()
      } else {
        // If QRCode contains data.
        try {
          // Converting the data to json format
          Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()

        } catch (e: JSONException) {
          e.printStackTrace()

          // Data not in the expected format. So, whole object as toast message.
          Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
        }

      }
    } else {
      super.onActivityResult(requestCode, resultCode, data)
    }
  }
}

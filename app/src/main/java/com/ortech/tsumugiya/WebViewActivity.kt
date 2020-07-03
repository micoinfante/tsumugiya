package com.ortech.tsumugiya

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_web_view.*


class WebViewActivity : AppCompatActivity() {

  companion object {
    const val ARG_URL = "url"
  }
  private var url: String? = null

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_web_view)

    setupToolBar()
    progressBarWebView.visibility = View.VISIBLE

    url = intent.getStringExtra(ARG_URL) as String

    if (Build.VERSION.SDK_INT >= 19) {
      webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }
    else {
      webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    webView.loadUrl(url!!)

    webView.settings.javaScriptEnabled = true
    webView.loadUrl(url!!)
//    webView.webViewClient =  object : WebViewClient() {
//      override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//        view?.loadUrl(url)
//        return false
//      }
//
//      override fun onPageFinished(view: WebView?, url: String?) {
//        super.onPageFinished(view, url)
//        progressBarWebView.visibility = View.INVISIBLE
//      }
//
//    }

  }

  private fun setupToolBar() {
    textViewWebViewDone.setOnClickListener {
      finish()
    }
  }
}

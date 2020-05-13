package com.ortech.shopapp

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.fragment_instagram.*


class InstagramFragment : Fragment() {
  
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_instagram, container, false)
  }

  @SuppressLint("SetJavaScriptEnabled")
  @RequiresApi(Build.VERSION_CODES.KITKAT)
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.d("InstagramFragment", "Loading instagram ${getString(R.string.instagram_url)}")
    val igWebView = instagramWebview
    igWebView.settings.javaScriptEnabled = true
    igWebView.webViewClient = WebViewClient()
    igWebView.loadUrl(getString(R.string.instagram_url))

  }


}

package com.ortech.shopapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.fragment_branch_details.*
import java.net.URI

class SettingsActivity: AppCompatActivity(), View.OnClickListener {

  private var isLoggedIn = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    // TODO Check user, chage button
    // TODO set onClicks on each button
    switchNotification.setOnClickListener(this)
    textViewSettingsPrivacy.setOnClickListener(this)
    textViewSettingsOpinion.setOnClickListener(this)
    textViewSettingsMediaInformation.setOnClickListener(this)
    buttonGoToSignIn.setOnClickListener(this)

    setupToolBar()
    checkUser()

  }

  private fun checkUser() {
    val user = Firebase.auth.currentUser
    if (user != null) {
      buttonGoToSignIn.text = "Sign out"
      isLoggedIn = true
    }
  }

  private fun setupToolBar() {
    toolbar.setNavigationOnClickListener {
      finish()
    }
  }

  override fun onClick(v: View?) {
    v?.let {

      when(it.id) {
        R.id.switchNotification -> {
          changeNotificationStatus()
        }
        R.id.textViewSettingsPrivacy -> {
          loadURL(Uri.parse(getString(R.string.home_link_notice)))
        }
        R.id.textViewSettingsOpinion -> {
          val intent = Intent(this, LoginSignUpActivity::class.java)
          startActivity(intent)
        }
        R.id.textViewSettingsMediaInformation -> {
          val intent = Intent(this, LoginSignUpActivity::class.java)
          startActivity(intent)
        }
        R.id.buttonGoToSignIn -> {
          if (isLoggedIn) {
            logoutUser()
          } else {
            val intent = Intent(this, LoginSignUpActivity::class.java)
            startActivity(intent)
          }
        } // MARK: -Goto Sign in button
      } // MARK: - End when check

    }// MARK: - End Null check
  }

  private fun changeNotificationStatus() {
    switchNotification.isChecked = true
  }

  private fun loadURL(uri: Uri) {
//    val intent = Intent(Intent.ACTION_VIEW)
//    intent.data = uri
//    startActivity(intent)
    val webView = WebView(this)
    setContentView(webView)
    webView.loadUrl(uri.toString())
  }

  private fun logoutUser() {
    if (isLoggedIn) {
      Firebase.auth.signOut()
      buttonGoToSignIn.text = getString(R.string.settings_sign_in)
      isLoggedIn = false
    }
  }

}
package com.ortech.shopapp

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Models.UserSingleton
import com.ortech.shopapp.ui.notifications.NotificationsViewModel
import kotlinx.android.synthetic.main.activity_settings.*

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
    switchNotification.isChecked = NotificationManagerCompat.from(baseContext).areNotificationsEnabled()
  }

  private fun checkUser() {
    val user = Firebase.auth.currentUser
    if (user != null) {
      isLoggedIn = true
    }

    if (checkEmailLoggedInUser() != null) {
      isLoggedIn = true
    }

    if (checkFacebookLoggedInUser() != null) {
      isLoggedIn = true
    }

     if (checkGoogleLoggedInUser()!= null) {
      isLoggedIn = true
    }
    if (isLoggedIn) {
      buttonGoToSignIn.text = "ログアウト"
    }

  }

  private fun setupToolBar() {
    toolbarHistory.setNavigationOnClickListener {
      finish()
    }
  }

  private fun checkGoogleLoggedInUser() : String? {
    val sharedPreferences = getSharedPreferences(
      getString(R.string.preference_UUID_key_google), Context.MODE_PRIVATE) ?: return null

    sharedPreferences.getString(getString(R.string.preference_UUID_key_google), null)
      .let { userUUID ->
        return if (userUUID != null) {
          UserSingleton.instance.setCurrentUserID(userUUID)
          Log.d(MainActivity.TAG, "current Google UUID is not null")
          userUUID
        } else {
          null
        }
      }
  }

  private fun checkFacebookLoggedInUser(): String? {
    val sharedPreferences = getSharedPreferences(
      getString(R.string.preference_UUID_key_facebook), Context.MODE_PRIVATE) ?: return null

    sharedPreferences.getString(getString(R.string.preference_UUID_key_facebook), null)
      .let { userUUID ->
        return if (userUUID != null) {
          UserSingleton.instance.setCurrentUserID(userUUID)
          Log.d(MainActivity.TAG, "current facebook UUID is not null")
          userUUID
        } else {
          null
        }
      }
  }

  private fun checkEmailLoggedInUser(): String?{
    val sharedPreferences = getSharedPreferences(
      getString(R.string.preference_UUID_key_email), Context.MODE_PRIVATE) ?: return null

    sharedPreferences.getString(getString(R.string.preference_UUID_key_email), null)
      .let { userUUID ->
        return if (userUUID != null) {
          UserSingleton.instance.setCurrentUserID(userUUID)
          Log.d(MainActivity.TAG, "current Google email is not null")
          userUUID
        } else {
          null
        }
      }
  }

  override fun onClick(v: View?) {
    v?.let {

      when(it.id) {
        R.id.switchNotification -> {
          changeNotificationStatus()
        }
        R.id.textViewSettingsPrivacy -> {
          val intent = Intent(this, WebViewActivity::class.java)
          intent.putExtra(WebViewActivity.ARG_URL, "https://www.foodconnection.jp/cookie/cookie_share.html")
          startActivity(intent)
//          loadURL(Uri.parse("https://www.foodconnection.jp/cookie/cookie_share.html"))
        }
        R.id.textViewSettingsOpinion -> {
          val intent = Intent(this, WebViewActivity::class.java)
          intent.putExtra(WebViewActivity.ARG_URL, "https://shop.ra-mensekai.co.jp/opinion.html")
          startActivity(intent)
        }
        R.id.textViewSettingsMediaInformation -> {
          val intent = Intent(this, WebViewActivity::class.java)
          intent.putExtra(WebViewActivity.ARG_URL, getString(R.string.home_link_notice))
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
    switchNotification.isChecked = !switchNotification.isChecked
    startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
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
      updatePreferences()

      val intent = Intent(baseContext, MainActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      startActivity(intent)
    }
  }

  private fun updatePreferences() {
    UserSingleton.instance.name = null

    val emailSharedPreferences = getSharedPreferences(
      getString(R.string.preference_UUID_key_email), Context.MODE_PRIVATE)

    with(emailSharedPreferences.edit()) {
      putString(getString(R.string.preference_UUID_key_email), null)
      apply()
    }

    val googleSharedPreferences = getSharedPreferences(
      getString(R.string.preference_UUID_key_google), Context.MODE_PRIVATE)

    with(googleSharedPreferences.edit()) {
      putString(getString(R.string.preference_UUID_key_google), null)
      apply()
    }

    val facebookSharedPreferences = getSharedPreferences(
      getString(R.string.preference_UUID_key_facebook), Context.MODE_PRIVATE)

    with(facebookSharedPreferences.edit()) {
      putString(getString(R.string.preference_UUID_key_facebook), null)
      apply()
    }
  }

}
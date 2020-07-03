package com.ortech.tsumugiya

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.zxing.qrcode.encoder.QRCode
import com.ortech.tsumugiya.Helpers.PreferenceHelper
import com.ortech.tsumugiya.Models.*
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

  private val db = Firebase.firestore

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

//    FacebookSdk.sdkInitialize(getApplicationContext());
//    AppEventsLogger.activateApp(this);
//    AppEventsLogger.activateApp(this, getString(R.string.facebook_app_id))

    var builder = NotificationCompat.Builder(this, 999.toString())
      .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
      .setContentTitle("This is a test title")
      .setContentText("This is a test content")
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    getStoreDetails()
    syncCMSSettings()

    if (Firebase.auth.currentUser != null) {
      getStaffAccountDetails()

      Handler().postDelayed({
        val qrIntent = Intent(baseContext, QRCodeScannerActivity::class.java)
        qrIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        qrIntent.putExtra(QRCodeScannerActivity.ARGS_TYPE, QRCodeScannerActivity.TYPE_STAFF)
        startActivity(qrIntent)
      }, SPLASH_DISPLAY_LENGTH.toLong())

    } else {
      val intent = Intent(this, BottomNavigationActivity::class.java)

      val emailUID = checkEmailLoggedInUser()
      val fbUID = checkFacebookLoggedInUser()
      val googleUID = checkGoogleLoggedInUser()

      when {
        emailUID != null -> {
          getUserDetails(emailUID)
        }
        fbUID != null -> {
          getUserDetails(fbUID)
        }
        googleUID != null -> {
          getUserDetails(googleUID)
        }
        else -> {
          checkUUID()
        }
      }
      updateFCMToken()

      Handler().postDelayed({
        startActivity(intent)
      }, SPLASH_DISPLAY_LENGTH.toLong())

    }
  }

  private fun getStaffAccountDetails() {
    val staffID = FirebaseAuth.getInstance().currentUser?.uid ?: return
    db.collection("CMSStaff")
      .whereEqualTo("userID", staffID)
      .get()
      .addOnSuccessListener { it ->
        if (it.documents.count() != 0) {
          val staffData = it.documents.first().toObject(StaffAccount::class.java)
          StaffSingleton.instance.currentStaff = staffData
        }
      }

  }

  private fun checkUUID() {
    val sharedPreferences = getSharedPreferences(
      getString(R.string.preference_UUID_key), Context.MODE_PRIVATE) ?: return
    val defaultUUID = UUID.randomUUID().toString()
    var currentUUID = defaultUUID

    sharedPreferences.getString(getString(R.string.preference_UUID_key), null)
      .let { userUUID ->
        if (userUUID != null) {
          currentUUID = userUUID
          UserSingleton.instance.setCurrentUserID(currentUUID)
          Log.d(TAG, "current UUID is not null")
        } else {
          with(sharedPreferences.edit()) {
            putString(getString(R.string.preference_UUID_key), defaultUUID)
            UserSingleton.instance.setCurrentUserID(defaultUUID)
            apply()
          }
        }
        getUserDetails(currentUUID)
      }
  }

  private fun checkGoogleLoggedInUser() : String? {
    val sharedPreferences = getSharedPreferences(
      getString(R.string.preference_UUID_key_google), Context.MODE_PRIVATE) ?: return null

    sharedPreferences.getString(getString(R.string.preference_UUID_key_google), null)
      .let { userUUID ->
        return if (userUUID != null) {
          UserSingleton.instance.setCurrentUserID(userUUID)
          Log.d(TAG, "current Google UUID is not null: $userUUID")
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
          Log.d(TAG, "current facebook UUID is not null")
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
          Log.d(TAG, "current email is not null")
          userUUID
        } else {
          null
        }
      }
  }

  private fun getStoreDetails() {
    db.collection("CMSStore")
      .get()
      .addOnSuccessListener {
        if (it.count() != 0) {
          if (it.first().id == "stores") {
            val mainID = it.first()["mainID"]
            val store = it.first()["store"] // storename
            val storeID = it.first()["storeID"]

            if (mainID != null) {
              UserSingleton.instance.mainID = mainID as String
            }

            if (store != null) {
              UserSingleton.instance.storeName = store as String
            }

            if (storeID != null) {
              UserSingleton.instance.storeID = storeID as String
            }

          }
        }
      }
  }

  private fun getUserDetails(userID: String) {
    // userID is document path/reference, userID field in document is random generated unique string

    db.collection("GlobalUsers").document(userID).get()
      .addOnSuccessListener { documentSnapshot ->
      if (documentSnapshot.exists()) {
        val globalUser = documentSnapshot.toObject(GlobalUser::class.java)
        val userName = documentSnapshot.get("name") as? String
        UserSingleton.instance.name = userName
        Log.d(TAG, globalUser.toString())
      } else {
        Log.d(TAG, "You have to create a new document, user is not existing in document/$userID")
        val newGlobalUser = GlobalUser(
          deviceUUID = userID,
          restoID =  UserSingleton.instance.mainID,
          restoName = UserSingleton.instance.appName,
          userID = userID
        )
        db.collection("GlobalUsers").document(userID).set(newGlobalUser)
          .addOnSuccessListener {
            Log.d(TAG, "Added new global user")
          }
          .addOnFailureListener {
            Log.e(TAG, "Failed to add new global user ${it.toString()}")
          }
        updateFCMToken()
      }
    }
      .addOnFailureListener { error ->
        Log.e(TAG, "get user details error: ${error.toString()}")
      }

    db.collection("TotalPoints").whereEqualTo("userID", userID)
      .get()
      .addOnSuccessListener {
        if (it.count() == 0) {
          createTotalPointsData(userID)
        }
      }
      .addOnFailureListener {

      }
  }

  private fun createTotalPointsData(userID: String) {

    db.collection("TotalPoints").add(
      hashMapOf(
        "userID" to userID,
        "totalPoints" to 0,
        "lastPoints" to 0,
        "pointsToday" to 0,
        "deviceUUID" to userID,
        "restoID" to UserSingleton.instance.mainID,
        "restoName" to UserSingleton.instance.appName,
        "dates" to Timestamp(Date()),
        "status" to "new"
      )
    )
  }


  private fun updateFCMToken() {

    val userUUID = UserSingleton.instance.userID
    UserSingleton.instance.setCurrentUserID(userUUID)
    FirebaseInstanceId.getInstance().instanceId
      .addOnCompleteListener{task ->
        if (!task.isSuccessful) {
          Log.w("Firebase Instance", "getInstanceId failed", task.exception)
        }

        val token = task.result?.token
        val data = hashMapOf("token" to token)
        UserSingleton.instance.fcmToken = token!!
        db.collection("GlobalUsers").document(userUUID).set(data, SetOptions.merge())
      }

  }

  private fun syncCMSSettings() {
    // Coupon Scan time limit
    db.collection("CMSBanner")
      .document("QRScanTimelimit")
      .get()
      .addOnSuccessListener {
        if (it.exists()) {
          CMSSettings.instance.setCurrentCouponTimeLimit(it.data?.get("second") as Number)
        }
      }

    // Customer scan qr code
    db.collection("CMSBanner")
      .document("timelimit")
      .get()
      .addOnSuccessListener {
        if (it.exists()) {
          CMSSettings.instance.setCurrentQRCodeTimeLimit(it.data?.get("second") as Number)
        }
      }

    // CMS Ranking
    db.collection("CMSRank")
      .get()
      .addOnSuccessListener {
        it.documents.forEach {ranking ->
          ranking?.let { pointRanking ->
            val newRanking = pointRanking.toObject(PointsRank::class.java)
            if (newRanking != null) {
              checkForDuplicates(newRanking)
            }
          }
        }
      }

  }

  private fun checkForDuplicates(pointsRank: PointsRank) {
    val rankingIDs = CMSSettings.instance.rankings.map { it.rankID }
    if (!rankingIDs.contains(pointsRank.rankID)) {
      CMSSettings.instance.rankings.add(pointsRank)
    }
  }




  companion object {
    const val TAG = "MainActivity"
    const val SPLASH_DISPLAY_LENGTH = 1000
  }

}

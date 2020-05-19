package com.ortech.shopapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Helpers.PreferenceHelper
import com.ortech.shopapp.Models.GlobalUser
import java.util.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

//        val intent = Intent(this, BottomNavigationActivity::class.java)
//        startActivity(intent)
    var builder = NotificationCompat.Builder(this, 999.toString())
      .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
      .setContentTitle("This is a test title")
      .setContentText("This is a test content")
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)

//        val intent = Intent(this, HomeScreen::class.java)
    val intent = Intent(this, BottomNavigationActivity::class.java)
//        val intent = Intent(this, StoreTabListActivity::class.java)


    FirebaseInstanceId.getInstance().instanceId
      .addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
          Log.w("Firebase Instance", "getInstanceId failed", task.exception)
          return@OnCompleteListener
        }

        // Get new Instance ID token
        val token = task.result?.token
        // Log and toast
//                val msg = getString("Got Firebase token: ", token)
        Log.d(TAG, "FCM $token!!")
//                Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
      })

    checkUUID()
    startActivity(intent)
  }

  private fun checkUUID() {
    val sharedPreferences = getPreferences(Context.MODE_PRIVATE) ?: return
    val defaultUUID = UUID.randomUUID().toString()
    var currentUUID = defaultUUID
    sharedPreferences.getString(getString(R.string.preference_UUID_key), defaultUUID)
      .let { userUUID ->
        if (userUUID != null) {
          currentUUID = userUUID
        } else {
          with(sharedPreferences.edit()) {
            putString(getString(R.string.preference_UUID_key), defaultUUID)
            commit()
          }
        }
        getUserDetails(currentUUID)
      }
  }

  private fun getUserDetails(userID: String) {
    // userID is document path/reference, userID field in document is random generated unique string
    val db = Firebase.firestore
    db.collection("GlobalUsers").document(userID).get()
      .addOnSuccessListener { documentSnapshot ->
      if (documentSnapshot.exists()) {
        val globalUser = documentSnapshot.toObject(GlobalUser::class.java)
        Log.d(TAG, globalUser.toString())
      } else {
        Log.d(TAG, "You have to create a new document, user is not existing in document/$userID")
        val newGlobalUser = GlobalUser(
          deviceUUID = userID,
          restoID = "qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP",
          restoName = "ラーメン世界",
          userID = UUID.randomUUID().toString()
        )
        db.collection("GlobalUsers").document(userID).set(newGlobalUser)
          .addOnSuccessListener {
            Log.d(TAG, "Added new global user")
          }
          .addOnFailureListener {
            Log.e(TAG, "Failed to add new global user ${it.toString()}")
          }
      }
    }
      .addOnFailureListener { error ->
        Log.e(TAG, "get user details error: ${error.toString()}")
      }
  }

  companion object {
    const val TAG = "MainActivity"
  }
}

package com.ortech.shopapp.Helpers

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ortech.shopapp.R
import java.util.*

class PreferenceHelper{

companion object {
  fun currentUUID(activity: AppCompatActivity): String {
    val res = Resources.getSystem()
    val sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE) ?: return ""
    val defaultUUID = UUID.randomUUID().toString()
    var currentUUID = defaultUUID
    sharedPreferences.getString(res.getString(R.string.preference_UUID_key), defaultUUID)
      .let { userUUID ->
        if (userUUID != null) {
          Log.d("Firebase Token UUID", userUUID)
        }
        if (userUUID != null) {
          currentUUID = userUUID
        } else {
          with(sharedPreferences.edit()) {
            putString(res.getString(com.ortech.shopapp.R.string.preference_UUID_key), defaultUUID)
            commit()
          }
        }
      }
    return currentUUID
  }

}



}
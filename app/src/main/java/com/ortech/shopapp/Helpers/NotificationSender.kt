package com.ortech.shopapp.Helpers

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.ortech.shopapp.BuildConfig
import com.squareup.okhttp.Callback
import com.squareup.okhttp.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


class NotificationSender {
  companion object {
    const val TAG = "NotificationSender"

    const val fcmURL = "https://fcm.googleapis.com/"
    private var retrofit: Retrofit? = null

    @JvmStatic
    fun getClient(): Retrofit? {
      if (retrofit == null) {
        retrofit = Retrofit.Builder()
          .baseUrl(fcmURL)
          .addConverterFactory(GsonConverterFactory.create())
          .build()
      }
      return retrofit
    }

    fun push() {
      val requestNotification = RequestNotification.build {
        token =
          "fW_m07Sdj-w:APA91bFRuGVplBbpQm6nkqf_j_wSvFWqTk0bizYnePBdF1Od96MbquWdAMpvm4hFCVrZEg-1xjTY3dkzn55fTZ-_v0kV9Jp-q_mNFN3R02V_ol-wzVLjlmqJOPSP8gbjyOHoOhe1gPMl"
        notification =
          NotificationModel(title = "This is from android", body = "Transferred points")
      }
      val apiClient = getClient()?.create(ApiInterface::class.java)
      val responseBodyCall =
        apiClient?.sendChatNotification(requestNotificaton = requestNotification)

      responseBodyCall!!.enqueue(object :
        retrofit2.Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
          Log.d(TAG, "Failed to send notification ${t.localizedMessage}")
        }

        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
          Log.d(TAG, "Notification sent ${response.toString()}")
        }

      })

    } // end push

  }
}

class RequestNotification (
  @SerializedName("to")
  val token: String,
  @SerializedName("notification")
  var notification: NotificationModel? = null
){

  constructor(builder: Builder) : this(builder.token, builder.notification)

  companion object {
    inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
  }

  class Builder{
    var token: String = ""
    var notification: NotificationModel? = null

    fun build() = RequestNotification(this)
  }

}

interface ApiInterface {
  @Headers("Authorization: key=AAAA2gm9e_I:APA91bE_ot0_kEIw5brVuvwKWhZz3ikwdTinTeGju-HqkkJPpFYjShlZJCn0Q0QsIa1LbBHTc_FQe9fWEYpsdbYI3vJ4bRXyZ5UzQeTYgqCGvOXagFkR7hsIEGQehitr8qQt-s6ddAI2",
    "Content-Type:application/json")
  @POST("fcm/send")
  fun sendChatNotification(@Body requestNotificaton: RequestNotification?): Call<ResponseBody>
}

data class NotificationModel (
  var title: String = "",
  var body: String = ""
)

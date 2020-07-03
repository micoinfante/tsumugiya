package com.ortech.tsumugiya.Helpers

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.ortech.tsumugiya.BuildConfig
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

    fun push(token: String, title: String, body: String) {
      val requestNotification = RequestNotification.build {
        this.token = token
        notification =
          NotificationModel(title = title, body = body)
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

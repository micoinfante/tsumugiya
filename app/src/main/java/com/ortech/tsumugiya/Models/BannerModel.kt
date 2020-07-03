package com.ortech.tsumugiya.Models

import android.webkit.WebMessage
import com.google.gson.annotations.SerializedName

data class Banner (
  val banner: Boolean = false,
  @SerializedName("bannerID")
  val id: String = "",
  val imageURL: String = "",
  val mainID: String = "",
  val status: String = ""
)

data class WebsiteInfo (
  val mainID: String = "",
  val webLink: String = "",
  val websub: String = "",
  val webMessage: String = ""
)
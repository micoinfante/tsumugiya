package com.ortech.shopapp.Models

import com.google.gson.annotations.SerializedName

data class Banner (
  val banner: Boolean = false,
  @SerializedName("bannerID")
  val id: String = "",
  val imageURL: String = "",
  val mainID: String = "",
  val status: String = ""
)

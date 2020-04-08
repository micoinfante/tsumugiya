package com.ortech.shopapp.Models

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class Coupon (
  @SerializedName("couponDetails")
  val details: String = "",
  @SerializedName("couponID")
  val id: String = "",
  @SerializedName("couponLabel")
  val label: String = "",
  val fromDate: Timestamp? = null,
  val imageURL: String = "",
  val isEnabled: String = "",
  val mainID: String = "",
  @SerializedName("orderBy")
  val order: String = "",
  val points: String = "",
  val selectedBranches: List<String> = ArrayList(),
  val untilDate: Timestamp? = null
)
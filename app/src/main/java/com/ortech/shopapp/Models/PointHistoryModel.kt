package com.ortech.shopapp.Models

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class PointHistory(
  val branchID: String = "",
  val branchName: String = "",
  val branchURL: String = "",
  val couponID: String = "",
  val couponItem: String = "",
  val customerEmail: String = "",
  val mainID: String = "",
  val points: Number = 0,
  val redeem: String = "",
  val staffEmail: String = "",
  val storeID: String = "",
  val storeName: String = "",
  val storeURL: String = "",
  @SerializedName("timeStamp")
  val timestamp: Timestamp? = null,
  val transfer: String = "",
  val userID: String = ""
)

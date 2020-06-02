package com.ortech.shopapp.Models

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class PointHistory(
  val branchID: String = "",
  val branchName: String = "",
  val branchURL: String = "",
  val couponID: String = "",
  val couponItem: String = "",
  val customerEmail: String = "",
  val mainID: String = "",
  val points: Int = 0,
  val redeem: String = "",
  val staffEmail: String = "",
  val storeID: String = "",
  val storeName: String = "",
  val storeURL: String = "",
  val timeStamp: Timestamp? = null,
  val transfer: String = "",
  val userID: String = ""
): Serializable{
  constructor(): this("")
}

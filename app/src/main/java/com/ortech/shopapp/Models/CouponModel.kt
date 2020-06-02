package com.ortech.shopapp.Models

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Coupon (
  val couponStore: String = "",
  val storeID: String = "",
  val couponDetails: String = "",
  val couponID: String = "",
  val couponLabel: String = "",
  val fromDate: Timestamp? = null,
  val imageURL: String = "",
  val isEnabled: String = "",
  val mainID: String = "",
  val orderBy: String = "",
  val points: String = "",
  val selectedBranches: List<String> = ArrayList(),
  val untilDate: Timestamp? = null
): Serializable{
  constructor() : this("")
}
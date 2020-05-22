package com.ortech.shopapp.Models

import com.google.gson.annotations.SerializedName

data class Branch (
  val branch: String = "",
  val location: String = "",
  val phone: String = "",
  val opening: String = "",
  val closing: String = "",
  val filename: String = "",
  val mainID: String = "",
  val branchID: String = "",
  val storeID: String = "",
  val userID: String = "",
  val access: String = "",
  val branchURLImages: String = "",
  val capacity: String = "",
  val credit: String = "",
  val exclusive: String = "",
  val holiday: String = "",
  val smartPhone: String = "",
  val orderBy: String = "",
  val latitude: Double = 0.0,
  val longitude: Double = 0.0

) {
//  constructor():this("",
//    "",
//    "",
//    "",
//    "",
//    "",
//    "",
//    "",
//    "",
//    "",
//    "",
//    "",
//    "",
//    "",
//    "",
//    "",
//    0,
//    0,
//    "",
//    "")
}
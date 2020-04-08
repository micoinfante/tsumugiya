package com.ortech.shopapp.Models

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class TotalPoints(
  @SerializedName("dates")
  val timestamp: Timestamp? = null,
  val deviceUUID: String = "",
  val lastPoints: Number = 0,
  val pointsToday: Number = 0,
  val restoID: String = "",
  val restoName: String = "",
  val totalPoints: Number = 0,
  val userID: String = ""
  )
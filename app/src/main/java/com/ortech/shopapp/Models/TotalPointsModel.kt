package com.ortech.shopapp.Models

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class TotalPoints(
  val dates: Timestamp? = null,
  val deviceUUID: String = "",
  val lastPoints: Int = 0,
  val pointsToday: Int = 0,
  val restoID: String = "",
  val restoName: String = "",
  val totalPoints: Int = 0,
  val userID: String = ""
  )
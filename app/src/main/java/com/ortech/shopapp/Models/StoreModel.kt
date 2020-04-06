package com.ortech.shopapp.Models

import com.google.gson.annotations.SerializedName

data class Store (
  val name: String,
  val address: String,
  val phone: String,
  val hours: String
)
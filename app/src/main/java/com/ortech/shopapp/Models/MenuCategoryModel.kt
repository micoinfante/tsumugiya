package com.ortech.shopapp.Models

import com.google.gson.annotations.SerializedName

data class MenuCategory (
  @SerializedName("categoryID")
  val id: String = "",
  @SerializedName("categoryLabel")
  val categoryLabel: String = "",
  val filename: String = "",
  val imageURL: String = "",
  val mainID: String = "",
  val selectedBranch: List<String> = ArrayList(),
  val selectedStore: List<String> = ArrayList()
)

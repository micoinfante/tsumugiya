package com.ortech.shopapp.Models

import com.google.gson.annotations.SerializedName

data class MenuCategory (
  @SerializedName("categoryID")
  val id: String = "",
  @SerializedName("categoryLabel")
  val name: String = "",
  val filename: String = "",
  val imageURL: String = "",
  val mainID: String = "",
  val orderBy: Number = 0,
  val selectedBranch: List<String> = ArrayList(),
  val selectedStore: List<String> = ArrayList()
)

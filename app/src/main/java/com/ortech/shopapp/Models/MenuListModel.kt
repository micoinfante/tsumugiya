package com.ortech.shopapp.Models

import com.google.gson.annotations.SerializedName

data class MenuList (
  val categoryID: String = "",
  @SerializedName("fileName")
  val filename: String = "",
  val imageURL: String = "",
  val mainID: String = "",
  @SerializedName("menuDetails")
  val details: String = "",
  @SerializedName("menuLabel")
  val label: String = "",
  @SerializedName("menuListID")
  val id: String = "",
  @SerializedName("menuPrice")
  val price: String = "",
  @SerializedName("orderBy")
  val order: Number = 0,
  val selectedBranches: List<String> = ArrayList(),
  val selectedStores: List<String> = ArrayList()
)

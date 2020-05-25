package com.ortech.shopapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MenuCategory (
  val categoryID: String = "",
  val categoryLabel: String = "",
  val filename: String = "",
  val imageURL: String = "",
  val mainID: String = "",
  val selectedBranch: List<String> = ArrayList(),
  val selectedStore: List<String> = ArrayList()
): Serializable {

}

package com.ortech.shopapp.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MenuList (
  val categoryID: String = "",
  val fileName: String = "",
  val imageURL: String = "",
  val mainID: String = "",
  val menuDetails: String = "",
  val menuLabel: String = "",
  val menuListID: String = "",
  val menuPrice: String = "",
  val selectedBranches: List<String> = ArrayList(),
  val selectedStores: List<String> = ArrayList()
): Serializable {

}

package com.ortech.shopapp.Models

// TODO("Create User type data class as generic type")
data class User(
  val email: String = "default",
  val password: String = "default",
  val restoID: String = "",
  val restoName: String = "",
  val userID: String = ""
) {
    constructor():this("default", "default", "","","" )
}

data class GlobalUser(
  val deviceUUID: String,
  val email: String = "default",
  val password: String = "default",
  val restoID: String = "",
  val restoName: String = "",
  val userID: String = "",
  val token: String = "",
  val device: String = "android"
) {
    constructor():this("", "default", "default")
}
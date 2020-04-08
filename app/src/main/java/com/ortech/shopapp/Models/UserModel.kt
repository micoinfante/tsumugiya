package com.ortech.shopapp.Models

// TODO("Create User type data class as generic type")
data class User(
  val email: String = "default",
  val password: String = "default",
  val restoID: String = "",
  val restoName: String = "",
  val userID: String = ""
)

// TODO("Create GlobalUser Data type - Inherit User type data to customize ")
data class GlobalUser(
  val user: User?,
  val deviceUUID: String,
  val userID: String
)
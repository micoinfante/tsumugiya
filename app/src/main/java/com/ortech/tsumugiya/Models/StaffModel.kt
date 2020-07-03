package com.ortech.tsumugiya.Models

import com.google.firebase.Timestamp
import java.io.Serializable


data class StaffAccount (
  val branch: String = "",
  val currentBranchID: String = "",
  val deviceID: String = "",
  val email: String = "",
  val mainBranchID: String = "",
  val manager: String = "",
  val password: String = "",
  val staffCreated: Timestamp? = null,
  val token: String = "",
  val userID: String  = ""
): Serializable{
  constructor() : this("")
}

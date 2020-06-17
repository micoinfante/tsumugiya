package com.ortech.shopapp.Models

import com.google.firebase.Timestamp


data class StaffAccount (
  val branch: String,
  val currentBranchID: String,
  val deviceID: String,
  val email: String,
  val mainBranchID: String,
  val manager: String,
  val password: String,
  val staffCreated: Timestamp,
  val token: String,
  val userID: String
)

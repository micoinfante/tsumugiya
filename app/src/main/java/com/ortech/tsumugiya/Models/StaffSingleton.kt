package com.ortech.tsumugiya.Models

class StaffSingleton private constructor() {

  var currentStaff: StaffAccount? = null

  private object HOLDER {
    val INSTANCE = StaffSingleton()
  }

  companion object {
    val instance: StaffSingleton by lazy {HOLDER.INSTANCE}
  }
}
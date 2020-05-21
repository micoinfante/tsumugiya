package com.ortech.shopapp.Models

class UserSingleton private constructor() {
  private var currentPoints = 0
  private var totalPoints = 0
  var userID = ""
  var fcmToken = ""

  fun setCurrentUserID(value: String) {
    this.userID = value
  }

  fun reduceCurrentPoints(by: Int) {
    this.currentPoints = currentPoints - by
  }

  fun setCurrentPoints(points: Int) {
    this.currentPoints = points
  }

  fun incrementCurrentPoints(by: Int) {
    this.currentPoints = currentPoints + by
    this.totalPoints = totalPoints + by
  }

  fun getCurrentPoints() : Int {
    return currentPoints
  }


  fun getTotalPoints() : Int {
    return totalPoints
  }

  //getter



  private object HOLDER {
    val INSTANCE = UserSingleton()
  }

  companion object {
    val instance: UserSingleton by lazy {HOLDER.INSTANCE}
  }
}
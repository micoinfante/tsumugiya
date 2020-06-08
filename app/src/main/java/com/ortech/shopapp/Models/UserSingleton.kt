package com.ortech.shopapp.Models

class UserSingleton private constructor() {
  // currentPoints = usable current points
  private var currentPoints = 0
  // total points are total points used
  private var totalPoints = 0
  // last points transferred
  private var lastPointsTransferred = 0
  // total points earned today
  private var pointsToday = 0



  var userID = ""
  var fcmToken = ""
  var isFacebookLoggedIn: Boolean = false
  var isGoogleLoggedIn: Boolean = false
  var email: String? = null
  var name: String? = null

  fun setCurrentUserID(value: String) {
    this.userID = value
  }

  fun reduceCurrentPoints(by: Int) {
    this.currentPoints = currentPoints - by
  }

  fun setCurrentPoints(points: Int) {
    this.currentPoints = points
  }

  fun setTotalPoints(points: Int) {
    this.totalPoints = this.totalPoints + points
  }

  fun getTotalPoints() : Int {
    return totalPoints
  }

  fun incrementCurrentPoints(by: Int) {
    this.currentPoints = currentPoints + by
    this.totalPoints = totalPoints + by
  }

  fun getCurrentPoints() : Int {
    return currentPoints
  }


  fun setLastPointsTransferred(points: Int) {
    this.lastPointsTransferred = this.lastPointsTransferred + points
  }

  fun getLastPointsTransferred() : Int {
    return this.lastPointsTransferred
  }

  fun setPointsToday(points: Int) {
    this.pointsToday = this.pointsToday + points
  }

  fun getPointsToday() : Int {
    return this.pointsToday
  }


  private object HOLDER {
    val INSTANCE = UserSingleton()
  }

  companion object {
    val instance: UserSingleton by lazy {HOLDER.INSTANCE}
  }

}
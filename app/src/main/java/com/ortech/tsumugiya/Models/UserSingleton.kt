package com.ortech.tsumugiya.Models

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserSingleton private constructor() {
  // currentPoints = usable current points
  private var currentPoints = 0
  // total points are total points used
  private var totalPoints = 0
  // last points transferred
  private var lastPointsTransferred = 0
  // total points earned today
  private var pointsToday = 0

  private var db = Firebase.firestore
  var oldUserID: String = ""

  val appName = "Tsumugiya"
  var mainID: String = "tsumugiyRestoIDa32yejdfjemfcijefmcmeflbn"
  var storeID: String = "sdnasd321ed10d1234"
  var storeName: String = "紬屋"



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


  fun transferPointsTo(docID: String, points: Int) {
    val data = hashMapOf(
      "totalPoints" to points,
      "status" to FieldValue.delete()
    )
    db.collection("TotalPoints")
      .document(docID)
      .set(data, SetOptions.merge())
      .addOnSuccessListener {

        resetPoints()
      }

  }

  private fun resetPoints() {
    val data = hashMapOf(
      "totalPoints" to 0,
      "lastPoints" to 0,
      "pointsToday" to 0
    )
    val totalPointsRef = db.collection("TotalPoints")

      totalPointsRef.whereEqualTo("userID", oldUserID)
      .get()
      .addOnSuccessListener {
        if (!it.isEmpty) {
          val docID = it.first().id
          totalPointsRef.document(docID)
            .set(data, SetOptions.merge())
        }
      }

  }


  private object HOLDER {
    val INSTANCE = UserSingleton()
  }

  companion object {
    val instance: UserSingleton by lazy {HOLDER.INSTANCE}
  }

}
package com.ortech.shopapp.Models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Coupon (
  val couponStore: String? = "",
  val storeID: String? = "",
  val couponDetails: String? = "",
  val couponID: String? = "",
  val couponLabel: String? = "",
  val fromDate: Timestamp? = null,
  val imageURL: String? = "",
  val isEnabled: String? = "",
  val mainID: String? = "",
  val orderBy: String? = "",
  val points: String? = "",
  val selectedBranches: ArrayList<String>? = ArrayList(),
  val untilDate: Timestamp? = null,
  val fromDateStr: String? = "",
  val untilDateStr: String? = ""
): Serializable, Parcelable{

  constructor(parcel: Parcel) : this(
    parcel.readString(),
    parcel.readString(),
    parcel.readString(),
    parcel.readString(),
    parcel.readString(),
    parcel.readParcelable(Timestamp::class.java.classLoader),
    parcel.readString(),
    parcel.readString(),
    parcel.readString(),
    parcel.readString(),
    parcel.readString(),
    parcel.createStringArrayList(),
    parcel.readParcelable(Timestamp::class.java.classLoader),
    parcel.readString(),
    parcel.readString()
  ) {
  }

  constructor() : this("")

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(couponStore)
    parcel.writeString(storeID)
    parcel.writeString(couponDetails)
    parcel.writeString(couponID)
    parcel.writeString(couponLabel)
    parcel.writeParcelable(fromDate, flags)
    parcel.writeString(imageURL)
    parcel.writeString(isEnabled)
    parcel.writeString(mainID)
    parcel.writeString(orderBy)
    parcel.writeString(points)
    parcel.writeStringList(selectedBranches)
    parcel.writeParcelable(untilDate, flags)
    parcel.writeString(fromDateStr)
    parcel.writeString(untilDateStr)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Coupon> {
    override fun createFromParcel(parcel: Parcel): Coupon {
      return Coupon(parcel)
    }

    override fun newArray(size: Int): Array<Coupon?> {
      return arrayOfNulls(size)
    }
  }
}
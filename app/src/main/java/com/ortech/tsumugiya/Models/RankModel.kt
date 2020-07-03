package com.ortech.tsumugiya.Models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


data class PointsRank(
    val filename: String?,
    val imageURL: String?,
    val mainID: String?,
    val orderBy: String?,
    val rankID: String?,
    val rankName: String?,
    val rankPoints: String?
): Serializable, Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    constructor(): this("","","","","","", "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(filename)
        parcel.writeString(imageURL)
        parcel.writeString(mainID)
        parcel.writeString(orderBy)
        parcel.writeString(rankID)
        parcel.writeString(rankName)
        parcel.writeString(rankPoints)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PointsRank> {
        override fun createFromParcel(parcel: Parcel): PointsRank {
            return PointsRank(parcel)
        }

        override fun newArray(size: Int): Array<PointsRank?> {
            return arrayOfNulls(size)
        }
    }

}
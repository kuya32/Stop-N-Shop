package com.macode.stopnshop.model

import android.os.Parcel
import android.os.Parcelable

data class Payment (
    val userID: String = "",
    val cardName: String = "",
    val cardNumber: String = "",
    val expirationMonth: String = "",
    val expirationYear: String = "",
    val verificationValue: String = "",
    val default: String = "",
    var id: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userID)
        parcel.writeString(cardName)
        parcel.writeString(cardNumber)
        parcel.writeString(expirationMonth)
        parcel.writeString(expirationYear)
        parcel.writeString(verificationValue)
        parcel.writeString(default)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Payment> {
        override fun createFromParcel(parcel: Parcel): Payment {
            return Payment(parcel)
        }

        override fun newArray(size: Int): Array<Payment?> {
            return arrayOfNulls(size)
        }
    }
}
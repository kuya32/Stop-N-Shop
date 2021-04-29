package com.macode.stopnshop.model

import android.os.Parcel
import android.os.Parcelable

data class User (
    var id: String = "",
    var dateUserCreated: String = "",
    var username: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var image: String = "",
    var phone: String = "",
    var location: String = "",
    var fcmToken: String = "",
    var status: String = "Online"
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(dateUserCreated)
        parcel.writeString(username)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
        parcel.writeString(image)
        parcel.writeString(phone)
        parcel.writeString(location)
        parcel.writeString(fcmToken)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
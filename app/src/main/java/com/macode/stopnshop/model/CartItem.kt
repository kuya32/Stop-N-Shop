package com.macode.stopnshop.model

import android.os.Parcel
import android.os.Parcelable

data class CartItem (
    val userID: String = "",
    val productOwnerID: String = "",
    val productID: String = "",
    val title: String = "",
    val price: String = "",
    val image: String = "",
    var cartQuantity: String = "",
    var stockQuantity: String = "",
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
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userID)
        parcel.writeString(productOwnerID)
        parcel.writeString(productID)
        parcel.writeString(title)
        parcel.writeString(price)
        parcel.writeString(image)
        parcel.writeString(cartQuantity)
        parcel.writeString(stockQuantity)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem {
            return CartItem(parcel)
        }

        override fun newArray(size: Int): Array<CartItem?> {
            return arrayOfNulls(size)
        }
    }
}
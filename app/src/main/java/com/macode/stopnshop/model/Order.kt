package com.macode.stopnshop.model

import android.os.Parcel
import android.os.Parcelable

data class Order(
    val userID: String = "",
    val items: ArrayList<CartItem> = ArrayList(),
    val address: Address = Address(),
    val payment: Payment = Payment(),
    val title: String = "",
    val image: String = "",
    val subTotalAmount: String = "",
    val waTaxAmount: String = "",
    val shippingAmount: String = "",
    val totalAmount: String = "",
    var id: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        arrayListOf<CartItem>().apply {
            parcel.readList(this, CartItem::class.java.classLoader)
        },
        parcel.readParcelable(Address::class.java.classLoader)!!,
        parcel.readParcelable(Payment::class.java.classLoader)!!,
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
        parcel.writeString(userID)
        parcel.writeList(items)
        parcel.writeParcelable(address, flags)
        parcel.writeParcelable(payment, flags)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(subTotalAmount)
        parcel.writeString(waTaxAmount)
        parcel.writeString(shippingAmount)
        parcel.writeString(totalAmount)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}
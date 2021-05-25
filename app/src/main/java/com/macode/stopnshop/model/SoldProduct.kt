package com.macode.stopnshop.model

import android.os.Parcel
import android.os.Parcelable

data class SoldProduct(
    val ownerID: String = "",
    val buyerID: String = "",
    val title: String = "",
    val price: String = "",
    val soldQuantity: String = "",
    val image: String = "",
    val orderID: String = "",
    val orderDate: String = "",
    val subTotalAmount: String = "",
    val waTaxAmount: String = "",
    val shippingAmount: String = "",
    val totalAmount: String = "",
    val address: Address = Address(),
    val payment: Payment = Payment(),
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
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(Address::class.java.classLoader)!!,
        parcel.readParcelable(Payment::class.java.classLoader)!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ownerID)
        parcel.writeString(buyerID)
        parcel.writeString(title)
        parcel.writeString(price)
        parcel.writeString(soldQuantity)
        parcel.writeString(image)
        parcel.writeString(orderID)
        parcel.writeString(orderDate)
        parcel.writeString(subTotalAmount)
        parcel.writeString(waTaxAmount)
        parcel.writeString(shippingAmount)
        parcel.writeString(totalAmount)
        parcel.writeParcelable(address, flags)
        parcel.writeParcelable(payment, flags)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SoldProduct> {
        override fun createFromParcel(parcel: Parcel): SoldProduct {
            return SoldProduct(parcel)
        }

        override fun newArray(size: Int): Array<SoldProduct?> {
            return arrayOfNulls(size)
        }
    }
}
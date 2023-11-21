package com.example.mywallet.classes


import android.os.Parcel
import android.os.Parcelable

data class Wallet(
    var id: Long,
    var walletName: String,
    var balance: Int,
    var currency: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(walletName)
        parcel.writeInt(balance)
        parcel.writeString(currency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Wallet> {
        override fun createFromParcel(parcel: Parcel): Wallet {
            return Wallet(parcel)
        }

        override fun newArray(size: Int): Array<Wallet?> {
            return arrayOfNulls(size)
        }
    }
}
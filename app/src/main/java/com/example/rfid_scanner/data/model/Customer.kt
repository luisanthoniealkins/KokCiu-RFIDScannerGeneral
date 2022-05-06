package com.example.rfid_scanner.data.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

class Customer : Parcelable {
    val code: String?
    val name: String?

    constructor(code: String?, name: String?) {
        this.code = code
        this.name = name
    }

    protected constructor(`in`: Parcel) {
        code = `in`.readString()
        name = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(code)
        dest.writeString(name)
    }

    companion object CREATOR : Creator<Customer> {
        override fun createFromParcel(parcel: Parcel): Customer {
            return Customer(parcel)
        }

        override fun newArray(size: Int): Array<Customer?> {
            return arrayOfNulls(size)
        }
    }
}
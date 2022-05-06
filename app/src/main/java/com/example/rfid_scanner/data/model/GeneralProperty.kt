package com.example.rfid_scanner.data.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

class GeneralProperty : Parcelable {
    val propertyCode: String?
    val propertyName: String?
    private var type: String? = null

    constructor(propertyCode: String?, propertyName: String?) {
        this.propertyCode = propertyCode
        this.propertyName = propertyName
    }

    protected constructor(`in`: Parcel) {
        propertyCode = `in`.readString()
        propertyName = `in`.readString()
        type = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(propertyCode)
        dest.writeString(propertyName)
        dest.writeString(type)
    }

    companion object CREATOR : Creator<GeneralProperty> {
        override fun createFromParcel(parcel: Parcel): GeneralProperty {
            return GeneralProperty(parcel)
        }

        override fun newArray(size: Int): Array<GeneralProperty?> {
            return arrayOfNulls(size)
        }
    }
}
package com.example.rfid_scanner.data.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.rfid_scanner.utils.helper.DateHelper
import com.example.rfid_scanner.utils.helper.TagHelper.TAG
import java.util.*

class Bill() : Parcelable {

    lateinit var billCode: String
    lateinit var customerCode: String
    lateinit var customerName: String
    lateinit var delivery: String
    lateinit var date: Date
    lateinit var reqStocks: MutableList<StockRequirement>

    var isAvailable: Boolean = false

    val formattedDate: String
        get() = DateHelper.getFormattedDateTime("yyyy-MM-dd", date)

    val formattedDateTime: String
        get() = DateHelper.getFormattedDateTime1(date)

    constructor(parcel: Parcel) : this() {
        billCode = parcel.readString().toString()
        customerCode = parcel.readString().toString()
        customerName = parcel.readString().toString()
        delivery = parcel.readString().toString()
        isAvailable = parcel.readByte() != 0.toByte()
    }

    constructor(encodedText: String) : this() {
        Log.d(TAG, "Encoded message: $encodedText")

        val billData = encodedText.split("/").toTypedArray()
        if (!(billData.size >= 4 && billData.size % 2 == 0)) return

        billCode = billData[0]
        customerCode = billData[2]
        delivery = billData[3]
        if (billData[1].length != 8) {
            Log.d(TAG, "Calendar length should be 8 chars")
            return
        }

        DateHelper.getDate("yyyyMMdd", billData[1]).let {
            if (it == null) {
                Log.d(TAG, "Calendar date format is wrong")
                return
            }
            date = it
        }

        reqStocks = mutableListOf()
        var i = 4
        while (i < billData.size) {
            try {
                reqStocks.firstOrNull { it.stock.code == billData[i] }
                    ?.incQuantity(billData[i + 1].toInt())
                    ?: run {
                        reqStocks.add(
                            StockRequirement(
                                Stock(code = billData[i]),
                                billData[i + 1].toInt()
                            )
                        )
                    }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                Log.d(TAG, "Quantity must be integer")
                return
            }
            i += 2
        }

        isAvailable = true
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(billCode)
        parcel.writeString(customerCode)
        parcel.writeString(customerName)
        parcel.writeString(delivery)
        parcel.writeByte(if (isAvailable) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Bill> {
        override fun createFromParcel(parcel: Parcel): Bill {
            return Bill(parcel)
        }

        override fun newArray(size: Int): Array<Bill?> {
            return arrayOfNulls(size)
        }
    }
}
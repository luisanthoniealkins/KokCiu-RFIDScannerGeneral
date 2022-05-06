package com.example.rfid_scanner.data.model

import android.util.Log
import com.example.rfid_scanner.utils.helper.DateHelper
import java.util.*

class Bill {
    lateinit var billCode: String
    lateinit var customerCode: String
    lateinit var customerName: String
    lateinit var delivery: String
    lateinit var date: Date
    lateinit var reqStocks: MutableList<StockRequirement>
    var isAvailable: Boolean

    val formattedDate: String
        get() = DateHelper.getFormattedDateTime("yyyy-MM-dd", date)

    val formattedDateTime: String
        get() = DateHelper.getFormattedDateTime1(date)


    constructor(encodedText: String) {
        Log.d("12345", encodedText)
        val billData = encodedText.split("/").toTypedArray()
        isAvailable = true
        if (!(billData.size >= 4 && billData.size % 2 == 0)) {
            isAvailable = false
            return
        }
        billCode = billData[0]
        customerCode = billData[2]
        delivery = billData[3]

        // Date
        if (billData[1].length != 8) {
            Log.d("12345", "calendar1")
            isAvailable = false
            return
        }

        DateHelper.getDate("yyyyMMdd", billData[1]).let {
            if (it == null) {
                Log.d("12345", "calendar")
                isAvailable = false
                return
            } else {
                date = it
            }
        }

        // Products
        reqStocks = mutableListOf()
        var i = 4
        while (i < billData.size) {
            try {
                reqStocks.firstOrNull { it.stock.code == billData[i] }?.incQuantity(billData[i + 1].toInt())
                    ?: run {
                        reqStocks.add(
                            StockRequirement(
                                Stock(
                                    code = billData[i]
                                ),
                                billData[i + 1].toInt()
                            )
                        )
                    }
            } catch (e: NumberFormatException) {
                isAvailable = false
                e.printStackTrace()
                Log.d("12345", "stock qty")
                return
            }
            i += 2
        }
    }
}
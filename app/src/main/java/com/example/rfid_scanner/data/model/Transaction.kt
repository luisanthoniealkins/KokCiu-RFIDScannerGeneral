package com.example.rfid_scanner.data.model

import com.example.rfid_scanner.utils.helper.DateHelper.getFormattedDateTime
import java.util.*

open class Transaction(
    val code: String?,
    val date: Date?,
    val type: String?
) {

    companion object {
        const val STATUS_MASUK = "MASUK"
        const val STATUS_KELUAR = "KELUAR"
        const val STATUS_RETUR = "RETUR"
        const val STATUS_RUSAK = "RUSAK"
        const val STATUS_HAPUS = "HAPUS"
        const val STATUS_PENYESUAIAN = "PENYESUAIAN"
        const val STATUS_PAKAI_ULANG = "PAKAI_ULANG"
        const val STATUS_CUSTOM = "CUSTOM"
    }

    private var quantity = 0
    private var mDetails: MutableList<TransactionDetail> = mutableListOf()

    fun getFormattedDate(format: String?): String {
        return getFormattedDateTime(format, date!!)
    }

    fun setQuantity(quantity: Int) {
        this.quantity = quantity
    }

    fun getQuantity(): Int {
        return quantity
    }

    fun setDetails(details: MutableList<TransactionDetail>) {
        mDetails = details
    }

    fun getDetails(): MutableList<TransactionDetail> {
        return mDetails
    }

    class CheckIn(code: String?, date: Date?) :
        Transaction(code, date, STATUS_MASUK)

    class CheckOut(code: String?, date: Date?, var customer: String, var delivery: String) :
        Transaction(code, date, STATUS_KELUAR)

    class Return(code: String?, date: Date?) :
        Transaction(code, date, STATUS_RETUR)

    class Broken(code: String?, date: Date?) :
        Transaction(code, date, STATUS_RUSAK)

    class Clear(code: String?, date: Date?) :
        Transaction(code, date, STATUS_HAPUS)

    class Lost(code: String?, date: Date?) :
        Transaction(code, date, STATUS_PENYESUAIAN)

    class Reuse(code: String?, date: Date?) :
        Transaction(code, date, STATUS_PAKAI_ULANG)

    class Other(code: String?, date: Date?) :
        Transaction(code, date, STATUS_CUSTOM)

    class TransactionDetail(
        val stockCode: String,
        val stockName: String,
        val stockVehicleType: String,
        val stockUnit: String,
        val stockQuantity: Int
    )
}
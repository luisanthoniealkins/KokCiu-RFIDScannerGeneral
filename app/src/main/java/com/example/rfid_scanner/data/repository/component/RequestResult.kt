package com.example.rfid_scanner.data.repository.component

import com.example.rfid_scanner.data.model.*
import com.example.rfid_scanner.data.model.Transaction.*
import com.example.rfid_scanner.data.model.Transaction.Companion.STATUS_HAPUS
import com.example.rfid_scanner.data.model.Transaction.Companion.STATUS_KELUAR
import com.example.rfid_scanner.data.model.Transaction.Companion.STATUS_MASUK
import com.example.rfid_scanner.data.model.Transaction.Companion.STATUS_PAKAI_ULANG
import com.example.rfid_scanner.data.model.Transaction.Companion.STATUS_PENYESUAIAN
import com.example.rfid_scanner.data.model.Transaction.Companion.STATUS_RETUR
import com.example.rfid_scanner.data.model.Transaction.Companion.STATUS_RUSAK
import com.example.rfid_scanner.data.model.repository.MResponse.ResponseData
import com.example.rfid_scanner.utils.helper.DateHelper
import com.example.rfid_scanner.utils.helper.LogHelper
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object RequestResult {

    /**
     * This RequestResult is used in handling success request
      */

    fun getGeneralResponse(response: JSONObject) = ResponseData(
        response.getString("response"),
        response.getString("message"),
        null
    )

    fun getAllStocks(response: JSONObject): ResponseData {
        val result = getGeneralResponse(response)

        val stocks = mutableListOf<StockRequirement>()
        val arr = response.getJSONArray("data")
        for (i in 0 until arr.length()) {
            val detail = arr[i] as JSONObject
            stocks.add(
                StockRequirement(
                    stock = Stock(
                        code = detail.getString("stock_code"),
                        name = detail.getString("stock_name"),
                        brand = detail.getString("stock_brand"),
                        vehicleType = detail.getString("stock_vehicle_type"),
                        unit = detail.getString("stock_unit"),
                        availableStock = detail.getInt("stock_available_stock")
                    ),
                    reqQuantity = detail.getInt("stock_available_stock")
                )
            )
        }
        result.data = stocks
        return result
    }

    fun getAllStockIds(response: JSONObject): ResponseData {
        val result = getGeneralResponse(response)

        val stockIds = mutableListOf<StockId>()
        val arr = response.getJSONArray("data")
        for (i in 0 until arr.length()) {
            val detail = arr[i] as JSONObject
            stockIds.add(
                StockId(
                    stock = Stock(
                        code = detail.getString("stock_code"),
                        name = detail.getString("stock_name"),
                        brand = detail.getString("stock_brand"),
                        vehicleType = detail.getString("stock_vehicle_type"),
                        unit = detail.getString("stock_unit"),
                        availableStock = detail.getInt("stock_available_stock")
                    ),
                    id = detail.getString("stock_id"),
                    unitCount = detail.getInt("stock_unit_count")
                )
            )
        }
        result.data = stockIds
        return result
    }

    fun getAllGeneralProperties(response: JSONObject): ResponseData {
        val result = getGeneralResponse(response)

        val stockProperties: ArrayList<GeneralProperty> = ArrayList<GeneralProperty>()
        val arr = response.getJSONArray("data")
        for (i in 0 until arr.length()) {
            val detail = arr[i] as JSONObject
            stockProperties.add(
                GeneralProperty(
                    propertyCode = detail.getString("property_code"),
                    propertyName = detail.getString("property_name"),
                )
            )
        }
        result.data = stockProperties
        return result
    }

    fun getRFIDs(response: JSONObject): ResponseData {
        val result = getGeneralResponse(response)

        val data = response.getJSONObject("data")
        val tags: ArrayList<Tag> = ArrayList<Tag>()
        val known = data.getJSONArray("known")
        for (i in 0 until known.length()) {
            val obj = known[i] as JSONObject
            val tagCode = obj.getString("rfid_code")
            val tagStatus = obj.getString("rfid_status")
            val stockId = obj.getString("stock_id")
            val stockName = obj.getString("stock_name")
            val stockUnitCount = obj.getInt("stock_unit_count")
            tags.add(Tag(tagCode, tagStatus, stockId, stockName, stockUnitCount))
        }
        val unknown = data.getJSONArray("unknown")
        for (i in 0 until unknown.length()) {
            val obj = unknown[i] as JSONObject
            val tagCode = obj.getString("rfid_code")
            tags.add(Tag(tagCode, Tag.STATUS_UNKNOWN, null, null, 0))
        }
        result.data = tags
        return result
    }

    fun validateBill(response: JSONObject) : ResponseData {
        val result = getGeneralResponse(response)
        result.data = response.getString("customer_name")
        return result
    }

    fun getStocks(response: JSONObject) : ResponseData {
        val result = getGeneralResponse(response)

        val stocks = mutableListOf<Stock>()
        val data = response.getJSONObject("data")
        val known = data.getJSONArray("known")
        for (i in 0 until known.length()) {
            val detail = known[i] as JSONObject
            val stockCode = detail.getString("stock_code")
            val stockName = detail.getString("stock_name")
            stocks.add(Stock(stockCode, stockName))
        }
        val unknown = data.getJSONArray("unknown")
        for (i in 0 until unknown.length()) {
            val detail = unknown[i] as JSONObject
            val stockCode = detail.getString("stock_code")
            stocks.add(Stock(stockCode))
        }
        result.data = stocks

        return result
    }

    fun getAllStockRFIDs(response: JSONObject): ResponseData {
        val result = getGeneralResponse(response)

        val tags = mutableListOf<Tag>()
        val arr = response.getJSONArray("data")
        for (i in 0 until arr.length()) {
            val detail = arr[i] as JSONObject
            val tagCode = detail.getString("rfid_code")
            val id = detail.getString("stock_id")
            val unitCount = detail.getInt("stock_unit_count")
            tags.add(Tag(tagCode, "-", id, "-", unitCount))
        }
        result.data = tags

        return result
    }

    fun getAllTransactionsDates(response: JSONObject): ResponseData {
        val result = getGeneralResponse(response)

        val dates = mutableListOf<String>()
        val arr = response.getJSONArray("data")
        for (i in 0 until arr.length()) {
            val date = arr[i] as String
            dates.add(
                DateHelper.getFormattedDateTime(
                    "MMMM, yyyy",
                    DateHelper.getDate("yyyy-MM-dd", date)!!
                )
            )
        }
        result.data = dates

        return result
    }

    fun getAllTransactions(response: JSONObject): ResponseData {
        val result = getGeneralResponse(response)

        val obj = response.getJSONObject("data")
        val arr = obj.getJSONArray("transactions")
        val transactions = mutableListOf<Transaction>()
        for (i in 0 until arr.length()) {
            val detail = arr[i] as JSONObject
//            LogHelper.postLog(detail.toString())
            val code = detail.getString("bill_code")
            val type = detail.getString("bill_type")
            val date: Date = DateHelper.getDate("yyyy-MM-dd hh:mm:ss", detail.getString("bill_date"))!!
            val transactionDetails = mutableListOf<TransactionDetail>()
            val arrDetail = detail["bill_stocks"] as JSONArray
            for (j in 0 until arrDetail.length()) {
                val stock = arrDetail[j] as JSONObject
                val stockCode = stock.getString("stock_code")
                val stockName = stock.getString("stock_name")
                val stockUnit = stock.getString("stock_unit")
                val stockVehicleType = stock.getString("stock_vehicle_type")
                val stockQuantity = stock.getString("stock_quantity").toInt()
                transactionDetails.add(
                    TransactionDetail(
                        stockCode,
                        stockName,
                        stockVehicleType,
                        stockUnit,
                        stockQuantity
                    )
                )
            }
            when (type) {
                STATUS_MASUK -> transactions.add(CheckIn(code, date))
                STATUS_KELUAR -> {
                    val customer = detail.getString("bill_customer")
                    val delivery = detail.getString("bill_delivery")
                    transactions.add(CheckOut(code, date, customer, delivery))
                }
                STATUS_RETUR -> transactions.add(Return(code, date))
                STATUS_RUSAK -> transactions.add(Broken(code, date))
                STATUS_HAPUS -> transactions.add(Clear(code, date))
                STATUS_PENYESUAIAN -> transactions.add(Lost(code, date))
                STATUS_PAKAI_ULANG -> transactions.add(Reuse(code, date))
                else -> transactions.add(Other(code, date))
            }
            transactions[transactions.size - 1].setDetails(transactionDetails)
        }
        result.data = transactions

        return result
    }
}


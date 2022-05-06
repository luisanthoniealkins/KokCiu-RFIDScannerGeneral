package com.example.rfid_scanner.data.repository.helper

import android.util.Log
import com.example.rfid_scanner.data.model.*
import com.example.rfid_scanner.utils.helper.DateHelper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object RequestParam {

    fun getRFIDS(tags: List<TagEPC>): JSONObject {
        val arr = JSONArray()
        tags.map { arr.put(it.epc) }

        val obj = JSONObject()
        obj.put("rfids_code", arr)
        return obj
    }

    fun transactionGeneral(
        bills: List<Bill>? = null,
        stockId: StockId? = null,
        stocks: List<Stock>? = null,
        tags: List<Tag>? = null,
        statusFrom: String,
        statusTo: String,
    ): JSONObject {
        var obj = JSONObject()
        if (bills.isNullOrEmpty()) {
            val newTags = mutableListOf<Tag>()
            tags?.map {
                newTags.add(
                    if (statusFrom == Tag.STATUS_UNKNOWN)
                        Tag(
                            epc = it.epc,
                            stockId = stockId?.id,
                            stockUnitCount = stockId?.unitCount ?: 0
                        )
                    else
                        it
                )
            }
            obj = getFormattedTransaction(groupByStocks(newTags))
            if (statusFrom == Tag.STATUS_UNKNOWN)
                obj.put("stock_id", stockId?.id)
        } else {
            val arr_bills = JSONArray()
            val arr_details = JSONArray()
            for (bill in bills) {
                val obj_bill = JSONObject()
                obj_bill.put("bill_code", bill.billCode)
                obj_bill.put("bill_date", bill.formattedDateTime)
                obj_bill.put("cust_code", bill.customerCode)
                obj_bill.put("bill_delivery", bill.delivery)
                arr_bills.put(obj_bill)
                bill.reqStocks.map {
                    val detail = JSONObject()
                    detail.put("stock_code", it.stock.code)
                    detail.put("bill_code", bill.billCode)
                    detail.put("quantity", it.reqQuantity)
                    arr_details.put(detail)
                }
            }
            obj.put("bills", arr_bills)
            obj.put("details", arr_details)
            obj.put("rfids", getRFIDsFromStocks(stocks))
        }
        obj.put("status_from", statusFrom)
        obj.put("status_to", statusTo)
        return obj
    }

    private fun getFormattedTransaction(stocks: List<Stock>): JSONObject {
        val obj = JSONObject()
        try {
            val billCode = DateHelper.getFormattedDateTimeCurrent("yyMMddhhmmss")
            obj.put("bills", getBillsFromRaw(billCode))
            obj.put("details", getDetailsFromStocks(billCode, stocks))
            obj.put("rfids", getRFIDsFromStocks(stocks))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return obj
    }

    private fun getBillsFromRaw(billCode: String): JSONArray {
        val arr_bills = JSONArray()
        val obj = JSONObject()
        obj.put("bill_code", billCode)
        obj.put("bill_date", DateHelper.getFormattedDateTimeCurrent("yyyy-MM-dd HH:mm:ss"))
        arr_bills.put(obj)
        return arr_bills
    }

    private fun getDetailsFromStocks(billCode: String, stocks: List<Stock>): JSONArray {
        val arr_details = JSONArray()
        for (stock in stocks) {
            val detail = JSONObject()
            detail.put("stock_code", stock.code)
            detail.put("bill_code", billCode)
            detail.put("quantity", stock.itemQuantity)
            arr_details.put(detail)
        }
        return arr_details
    }

    private fun groupByStocks(tags: List<Tag>?): List<Stock> {
        val stocks = mutableListOf<Stock>()
        tags?.map { tag ->
            if (stocks.none { it.code == tag.stockCode }) stocks.add(Stock(code = tag.stockCode!!))
            stocks.firstOrNull{ it.code == tag.stockCode }?.addItem(tag.epc, tag.stockUnitCount)
        }
        return stocks
    }

    private fun getRFIDsFromStocks(stocks: List<Stock>?): JSONArray {
        val arr_rfids = JSONArray()
        stocks?.map {
            it.items.map { tag ->
                arr_rfids.put(tag)
            }
        }
        return arr_rfids
    }

}
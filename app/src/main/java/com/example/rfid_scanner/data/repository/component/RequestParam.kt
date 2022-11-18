package com.example.rfid_scanner.data.repository.component

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
            val arrBills = JSONArray()
            val arrDetails = JSONArray()
            for (bill in bills) {
                val objBill = JSONObject()
                objBill.put("bill_code", bill.billCode)
                objBill.put("bill_date", bill.formattedDateTime)
                objBill.put("cust_code", bill.customerCode)
                objBill.put("bill_delivery", bill.delivery)
                arrBills.put(objBill)
                bill.reqStocks.map {
                    val detail = JSONObject()
                    detail.put("stock_code", it.stock.code)
                    detail.put("bill_code", bill.billCode)
                    detail.put("quantity", it.reqQuantity)
                    arrDetails.put(detail)
                }
            }
            obj.put("bills", arrBills)
            obj.put("details", arrDetails)
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
        val arrBills = JSONArray()
        val obj = JSONObject()
        obj.put("bill_code", billCode)
        obj.put("bill_date", DateHelper.getFormattedDateTimeCurrent("yyyy-MM-dd HH:mm:ss"))
        arrBills.put(obj)
        return arrBills
    }

    private fun getDetailsFromStocks(billCode: String, stocks: List<Stock>): JSONArray {
        val arrDetails = JSONArray()
        for (stock in stocks) {
            val detail = JSONObject()
            detail.put("stock_code", stock.code)
            detail.put("bill_code", billCode)
            detail.put("quantity", stock.itemQuantity)
            arrDetails.put(detail)
        }
        return arrDetails
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
        val arrRfids = JSONArray()
        stocks?.map {
            it.items.map { tag ->
                arrRfids.put(tag)
            }
        }
        return arrRfids
    }

    fun validateBill(bill: Bill): JSONObject {
        val obj = JSONObject()
        obj.put("bill_code", bill.billCode)
        obj.put("customer_code", bill.customerCode)
        return obj
    }

    fun getStocks(reqStocks: MutableList<StockRequirement>): JSONObject {
        val obj = JSONObject()

        val arr = JSONArray()
        for (stock in reqStocks) arr.put(stock.stock.code)
        obj.put("stocks_code", arr)
        obj.put("index", 0)

        return obj
    }

    fun getAllStockRFIDS(stockCode: String): JSONObject {
        val obj = JSONObject()
        obj.put("stock_code", stockCode)
        return obj
    }

}
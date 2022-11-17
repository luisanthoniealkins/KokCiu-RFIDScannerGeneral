package com.example.rfid_scanner.data.repository.component

import com.example.rfid_scanner.data.model.*
import com.example.rfid_scanner.data.model.repository.MResponse.ResponseData
import org.json.JSONException
import org.json.JSONObject

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
}


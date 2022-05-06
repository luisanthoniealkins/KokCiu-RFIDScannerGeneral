package com.example.rfid_scanner.data.repository.helper

import com.example.rfid_scanner.data.model.Stock
import com.example.rfid_scanner.data.model.StockRequirement
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.data.model.repository.MResponse.ResponseData
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class RequestResult {



    companion object {

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
                val code = detail.getString("stock_code")
                val name = detail.getString("stock_name")
                val brand = detail.getString("stock_brand")
                val vehicleType = detail.getString("stock_vehicle_type")
                val unit = detail.getString("stock_unit")
                val availableStock = detail.getInt("stock_available_stock")
                stocks.add(
                    StockRequirement(
                        Stock(
                            code,
                            name,
                            brand,
                            vehicleType,
                            unit,
                            availableStock
                        ),
                        availableStock
                    )
                )
            }
            result.data = stocks

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
    }

}
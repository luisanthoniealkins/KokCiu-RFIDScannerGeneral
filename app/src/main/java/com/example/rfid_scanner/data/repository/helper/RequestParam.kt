package com.example.rfid_scanner.data.repository.helper

import com.example.rfid_scanner.data.model.TagEPC
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class RequestParam {

    companion object {
        fun getRFIDS(tags: ArrayList<TagEPC>): JSONObject {
            val arr = JSONArray()
            tags.map { arr.put(it.epc) }

            val obj = JSONObject()
            obj.put("rfids_code", arr)
            return obj
        }
    }

}
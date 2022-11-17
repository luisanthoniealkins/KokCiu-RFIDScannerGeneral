package com.example.rfid_scanner.utils.helper

import android.util.Log

object LogHelper {

    fun postLog(text: String) {
        Log.d(TagHelper.TAG, text)
    }

}
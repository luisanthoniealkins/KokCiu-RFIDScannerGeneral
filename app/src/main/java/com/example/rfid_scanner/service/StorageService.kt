package com.example.rfid_scanner.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class StorageService(private val context: Context) {

    companion object {
        private const val SP_LAST_DEVICE = "sp_last_device"
        private const val SP_IP = "sp_ip"
        private const val SP_PORT = "sp_port"
        private const val SP_WIFI = "sp_wifi"
        private const val SP_LAST_STOCK_ID = "sp_last_stock_id"
        private const val SP_LAST_STOCK_NAME = "sp_last_stock_name"
        private const val SP_MIN_EPC_LENGTH = "sp_min_epc_length"
        private const val SP_MAX_EPC_LENGTH = "sp_max_epc_length"
        private const val SP_EPC_DIFF_TOLERANCE = "sp_epc_diff_tolerance"

        @SuppressLint("StaticFieldLeak")
        var mInstance: StorageService? = null
            private set

        fun getI() = mInstance!!

        fun init(context: Context) { mInstance = StorageService(context) }
    }

    private fun getPref(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    var lastConnectedBluetoothDevice
        set(value) { getPref().edit().putString(SP_LAST_DEVICE, value).apply() }
        get() = getPref().getString(SP_LAST_DEVICE, "-")

    var ipAddress
        set(value) { getPref().edit().putString(SP_IP, value).apply() }
        get() = getPref().getString(SP_IP, "192.168.43.109")

    var port
        set(value) { getPref().edit().putString(SP_PORT, value).apply() }
        get() = getPref().getString(SP_PORT, "80")

    var wifi
        set(value) { getPref().edit().putString(SP_WIFI, value).apply() }
        get() = getPref().getString(SP_WIFI, "HUAWE-Mj")

    var lastUsedStockId
        set(value) { put(SP_LAST_STOCK_ID, value)}
        get() = get(SP_LAST_STOCK_ID, "12020-42000 HYK#Q1")

    var lastUsedStockName
        set(value) { put(SP_LAST_STOCK_NAME, value) }
        get() = get(SP_LAST_STOCK_NAME, "PION PIRING BELAKANG 6X40")

    var minEPCLength
        set(value) { putInt(SP_MIN_EPC_LENGTH, value) }
        get() = getInt(SP_MIN_EPC_LENGTH, 24)

    var maxEPCLength
        set(value) { putInt(SP_MAX_EPC_LENGTH, value) }
        get() = getInt(SP_MAX_EPC_LENGTH, 24)

    var epcDiffTolerance
        set(value) { putInt(SP_EPC_DIFF_TOLERANCE, value) }
        get() = getInt(SP_EPC_DIFF_TOLERANCE, 3)

    fun setStatusChecked(from: String, to: String, isChecked: Boolean) {
        val st = "${from}>${to}"
        getPref().edit().putBoolean(st, isChecked).apply()
    }

    fun isStatusChecked(from: String, to: String): Boolean {
        val st = "${from}>${to}"
        return getPref().getBoolean(st, false)
    }

    private fun put(key: String, value: String?) = getPref().edit().putString(key, value).apply()
    private fun get(key: String, default: String) = getPref().getString(key, default)

    private fun putInt(key: String, value: Int) = getPref().edit().putInt(key, value).apply()
    private fun getInt(key: String, default: Int) = getPref().getInt(key, default)
}
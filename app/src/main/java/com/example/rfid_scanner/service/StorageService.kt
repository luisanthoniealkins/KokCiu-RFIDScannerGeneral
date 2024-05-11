package com.example.rfid_scanner.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.rfid_scanner.data.model.BTDeviceConfig
import com.example.rfid_scanner.utils.constant.Constant
import com.example.rfid_scanner.utils.constant.Constant.BluetoothDeviceType.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

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
        private const val SP_QR_CODE_DELIMITER = "sp_qr_code_delimiter"

        private const val SP_BT_DEVICE_CONFIGS = "sp_bt_device_configs"

        val storage
            get() = getI()

        @SuppressLint("StaticFieldLeak")
        var mInstance: StorageService? = null
            private set

        fun getI() = mInstance!!

        fun init(context: Context) {
            mInstance = StorageService(context)
        }
    }

    private fun getPref(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    var lastConnectedBluetoothDevice
        set(value) = put(SP_LAST_DEVICE, value)
        get() = get(SP_LAST_DEVICE, "-")

    var ipAddress
        set(value) = put(SP_IP, value)
        get() = get(SP_IP, "192.168.43.109")

    var port
        set(value) = put(SP_PORT, value)
        get() = get(SP_PORT, "80")

    var wifi
        set(value) = put(SP_WIFI, value)
        get() = get(SP_WIFI, "HUAWE-Mj")

    var lastUsedStockId
        set(value) = put(SP_LAST_STOCK_ID, value)
        get() = get(SP_LAST_STOCK_ID, "12020-42000 HYK#Q1")

    var lastUsedStockName
        set(value) = put(SP_LAST_STOCK_NAME, value)
        get() = get(SP_LAST_STOCK_NAME, "PION PIRING BELAKANG 6X40")

    var minEPCLength
        set(value) = putInt(SP_MIN_EPC_LENGTH, value)
        get() = getInt(SP_MIN_EPC_LENGTH, 24)

    var maxEPCLength
        set(value) = putInt(SP_MAX_EPC_LENGTH, value)
        get() = getInt(SP_MAX_EPC_LENGTH, 24)

    var epcDiffTolerance
        set(value) = putInt(SP_EPC_DIFF_TOLERANCE, value)
        get() = getInt(SP_EPC_DIFF_TOLERANCE, 3)

    var qrCodeDelimiter
        set(value) = put(SP_QR_CODE_DELIMITER, value)
        get() = get(SP_QR_CODE_DELIMITER, "/")

    val btDeviceConfigs =
        Gson().fromJson<MutableList<BTDeviceConfig>?>(
            get(SP_BT_DEVICE_CONFIGS, ""),
            object : TypeToken<List<BTDeviceConfig?>?>() {}.type
        )?.toMutableList() ?: mutableListOf(
            BTDeviceConfig("66:32:26:32:44:45", Printer),
            BTDeviceConfig("98:DA:B0:00:3D:59", ScannerShortRange),
            BTDeviceConfig("FE:88:A5:83:64:98", ScannerLongRange),
            BTDeviceConfig("C3:3E:87:93:C3:8F", ScannerLongRange,4,4),
        )

    fun saveBtDeviceConfigs() = put(SP_BT_DEVICE_CONFIGS, Gson().toJson(btDeviceConfigs))

    fun setStatusChecked(from: String, to: String, isChecked: Boolean) = getPref().edit().putBoolean("${from}>${to}", isChecked).apply()
    fun isStatusChecked(from: String, to: String) = getPref().getBoolean("${from}>${to}", false)

    private fun put(key: String, value: String?) = getPref().edit().putString(key, value).apply()
    private fun get(key: String, default: String) = getPref().getString(key, default) ?: default

    private fun putInt(key: String, value: Int) = getPref().edit().putInt(key, value).apply()
    private fun getInt(key: String, default: Int) = getPref().getInt(key, default)
}
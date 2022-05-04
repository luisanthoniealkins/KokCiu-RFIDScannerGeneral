package com.example.rfid_scanner.utils.service

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

        @SuppressLint("StaticFieldLeak")
        var mInstance: StorageService? = null
            private set

        fun getInstance() = mInstance!!

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

}
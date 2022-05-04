package com.example.rfid_scanner.utils.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class NetworkService(private val context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mInstance: NetworkService? = null
            private set

        fun getInstance() = mInstance!!

        fun init(context: Context) { mInstance = NetworkService(context) }
    }

    private val mWifiManager: WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val mConnectivityManager: ConnectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _sfStatus = MutableStateFlow(false)
    val sfStatus = _sfStatus.asStateFlow()

    fun stopService() {
        context.unregisterReceiver(mWifiStateReceiver)
        stop()
    }

    val isWifiEnabled: Boolean
        get() = mWifiManager.isWifiEnabled

    val isWifiConnected: Boolean
        get() {
            val nInfo = mConnectivityManager.activeNetworkInfo ?: return false
            return if (!nInfo.isConnected) false else nInfo.type == ConnectivityManager.TYPE_WIFI
        }

    val wifiSSID: String
        get() {
            val ssid = mWifiManager.connectionInfo.ssid
            return ssid.substring(1, ssid.length - 1)
        }

    val wifiStrength: Int
        get() = WifiManager.calculateSignalLevel(mWifiManager.connectionInfo.rssi, 100)

    fun updateStatus() {
        _sfStatus.value = true
    }


    private val mWifiStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) { updateStatus(); }
    }

    private var timer: Timer? = null
    private val timerTask: TimerTask = object : TimerTask() {
        override fun run() { updateStatus(); }
    }

    private fun start() {
        if (timer != null) return
        timer = Timer()
        timer!!.scheduleAtFixedRate(timerTask, 0, 5000)
    }

    fun stop() {
        if (timer == null) return
        timer!!.cancel()
        timer = null
    }

    init {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(mWifiStateReceiver, intentFilter)
        start()
    }


}
package com.example.rfid_scanner.module.main

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.status.BluetoothStatus
import com.example.rfid_scanner.data.model.status.MConnectionStatus
import com.example.rfid_scanner.data.model.status.NetworkStatus
import com.example.rfid_scanner.data.model.status.ServerStatus
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.helper.RequestEndPoint
import com.example.rfid_scanner.utils.constant.Constant.SERVICE_STATUS_ERROR
import com.example.rfid_scanner.utils.constant.Constant.SERVICE_STATUS_OK
import com.example.rfid_scanner.utils.generic.BaseViewModel
import com.example.rfid_scanner.utils.generic.observeOnce
import com.example.rfid_scanner.utils.service.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : BaseViewModel() {

    companion object {
        val hideStatusDestination = setOf(
            R.id.deviceListFragment,
            R.id.networkSettingFragment
        )
    }

    private lateinit var mNetworkService : NetworkService
    private lateinit var mServerService : VolleyRepository
    lateinit var mBluetoothScannerService : BluetoothScannerService

    private val _ldNetworkStatus = MutableLiveData<NetworkStatus>()
    val ldNetworkStatus : LiveData<NetworkStatus> = _ldNetworkStatus

    private val _ldServerStatus = MutableLiveData<ServerStatus>()
    val ldServerStatus : LiveData<ServerStatus> = _ldServerStatus

    private val _ldBluetoothStatus = MutableLiveData<BluetoothStatus>()
    val ldBluetoothStatus : LiveData<BluetoothStatus> = _ldBluetoothStatus

    fun initService() {
        mNetworkService = NetworkService.getInstance()
        mServerService = VolleyRepository.getInstance()
        mBluetoothScannerService = BluetoothScannerService.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            launch { mNetworkService.sfStatus.collect{ refreshNetworkStatus() } }
            launch { mServerService.sfStatus.collect{ refreshServerStatus(it) } }
            launch { mBluetoothScannerService.sfStatus.collect{ refreshBluetoothStatus(it) } }
        }
//        checkServer()
    }

    fun stopService() {
        mNetworkService.stopService()
        mBluetoothScannerService.disconnectBluetooth()
    }

    fun checkServer() {
        VolleyRepository.getInstance().requestAPI(RequestEndPoint.CHECK_SERVER).let {
            it.data.observeOnce {
//                ToastHelper.showToast(context, "aman")
            }
            it.error.observeOnce {
//                ToastHelper.showToast(context, "gagal")
            }
        }
    }

    private fun refreshNetworkStatus() {
        val ns = NetworkStatus(SERVICE_STATUS_ERROR, R.color.red_disconnect, "-", 0,"-")

        if (!mNetworkService.isWifiEnabled) ns.message = "Wifi offline"
        else if (!mNetworkService.isWifiConnected) ns.message = "Wifi belum terhubung"
        else if (!StorageService.getInstance().wifi.equals(mNetworkService.wifiSSID)) {
            ns.ssid = mNetworkService.wifiSSID
            ns.message = "Diharapkan ${StorageService.getInstance().wifi}"
        } else {
            ns.ssid = mNetworkService.wifiSSID
            ns.status = SERVICE_STATUS_OK

            val wifiStrength: Int = mNetworkService.wifiStrength
            ns.strength = wifiStrength

            when {
                wifiStrength < 30 -> {
                    ns.statusColor = R.color.red_disconnect
                    ns.message = "Sinyal lemah"
                }
                wifiStrength < 70 -> {
                    ns.statusColor = R.color.yellow_warning
                    ns.message = "Sinyal lemah"
                }
                else ->
                    ns.statusColor = R.color.green_connect
            }
        }

        _ldNetworkStatus.postValue(ns)
    }

    private fun refreshServerStatus(isConnected: Boolean) {
        val ss = ServerStatus(SERVICE_STATUS_ERROR, R.color.red_disconnect, "-")
        if (isConnected) {
            ss.status = SERVICE_STATUS_OK
            ss.statusColor = R.color.green_connect
        } else {
            ss.message = "Pastikan koneksi benar (wifi, IP, port) dan server telah dinyalakan "
        }
        _ldServerStatus.postValue(ss)
    }

    private fun refreshBluetoothStatus(status: MConnectionStatus) {
        val bs = BluetoothStatus(SERVICE_STATUS_ERROR, R.color.red_disconnect, "-", "-",0f, "-")

        if (!mBluetoothScannerService.isBluetoothEnabled) bs.message = "Bluetooth offline"
        else if (!status.isConnected) bs.message = "Bluetooth belum terhubung"
        else if (status.isFailed) bs.message = "Bluetooth gagal terhubung"
        else {
            val device: BluetoothDevice? = mBluetoothScannerService.currentDevice
            bs.status = SERVICE_STATUS_OK
            bs.statusColor = R.color.green_connect
            bs.deviceName = device?.name ?: "no_name"
            bs.deviceAddress = device?.address ?: "no_address"
        }

        _ldBluetoothStatus.postValue(bs)
    }
}
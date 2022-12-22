package com.example.rfid_scanner.module.main

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.status.BluetoothStatus
import com.example.rfid_scanner.data.model.status.MConnectionStatus
import com.example.rfid_scanner.data.model.status.NetworkStatus
import com.example.rfid_scanner.data.model.status.ServerStatus
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.utils.constant.Constant.SERVICE_STATUS_ERROR
import com.example.rfid_scanner.utils.constant.Constant.SERVICE_STATUS_OK
import com.example.rfid_scanner.service.*
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : BaseViewModel() {

    companion object {
        val hideStatusDestination = setOf(
            R.id.deviceListFragment,
            R.id.settingsTagFragment,
            R.id.networkSettingFragment,
            R.id.transSettingFragment,
            R.id.exploreStockFragment,
            R.id.exploreStockIdFragment,
            R.id.explorePropertyFragment,
            R.id.alterPropertyFragment,
            R.id.qrReaderFragment,
            R.id.historyTransactionFragment,
            R.id.historyStockFragment,
            R.id.alterStockIdFragment,
            R.id.alterStockFragment,
            R.id.settingsBluetoothFragment,
            R.id.transactionRFIDFragment
        )

        @RequiresApi(Build.VERSION_CODES.S)
        val higherOrEqualThanCodeSPermissions = listOf(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
        )

        val lowerThanCodeSPermissions = listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
        )

        val allCodePermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.NFC,
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
        mServerService = VolleyRepository.getI()
        mBluetoothScannerService = BluetoothScannerService.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            launch { mNetworkService.sfStatus.collect{ refreshNetworkStatus() } }
            launch { mServerService.sfStatus.collect{ refreshServerStatus(it) } }
            launch { mBluetoothScannerService.sfStatus.collect{ refreshBluetoothStatus(it) } }
        }
        checkServer()
    }

    fun stopService() {
        mNetworkService.stopService()
        mBluetoothScannerService.disconnectBluetooth()
    }

    fun checkServer() {
        VolleyRepository.getI().requestAPI(RequestEndPoint.CHECK_SERVER)
    }

    fun refreshNetworkStatus() {
        val ns = NetworkStatus(SERVICE_STATUS_ERROR, R.color.red_disconnect, "-", 0,"-", null)

        if (!mNetworkService.isWifiEnabled) ns.message = "Wifi offline"
        else if (!mNetworkService.isWifiConnected) ns.message = "Wifi belum terhubung"
        else if (!StorageService.getI().wifi.equals(mNetworkService.wifiSSID)) {
            ns.ssid = mNetworkService.wifiSSID
            ns.message = "Diharapkan ${StorageService.getI().wifi}"
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

    private fun refreshServerStatus(isConnected: Boolean?) {
        isConnected?.let {
            val ss = ServerStatus(SERVICE_STATUS_ERROR, R.color.red_disconnect, "-", null)
            if (it) {
                ss.status = SERVICE_STATUS_OK
                ss.statusColor = R.color.green_connect
            } else {
                ss.message = "Pastikan koneksi benar (wifi, IP, port) dan server telah dinyalakan"
                ss.toastMessage = "Koneksi server gagal"
            }
            _ldServerStatus.postValue(ss)
        }
    }

    @SuppressLint("MissingPermission")
    private fun refreshBluetoothStatus(status: MConnectionStatus) {
        val bs = BluetoothStatus(SERVICE_STATUS_ERROR, R.color.red_disconnect, "-", "-",0f, "-", null)

        if (!mBluetoothScannerService.isBluetoothEnabled) bs.message = "Bluetooth offline"
        else if (!status.isConnected) bs.message = "Bluetooth belum terhubung"
        else if (status.isFailed) {
            bs.message = "Bluetooth gagal terhubung"
            bs.toastMessage = "Koneksi bluetooth gagal"
        } else {
            val device: BluetoothDevice? = mBluetoothScannerService.currentDevice
            bs.status = SERVICE_STATUS_OK
            bs.statusColor = R.color.green_connect
            bs.deviceName = device?.name ?: "no_name"
            bs.deviceAddress = device?.address ?: "no_address"
        }

        _ldBluetoothStatus.postValue(bs)
    }

    fun getSupportedPermissions(): Array<String> {
        val permissions = mutableListOf<String>()
        permissions.addAll(allCodePermissions)
        permissions.addAll(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) higherOrEqualThanCodeSPermissions
            else lowerThanCodeSPermissions
        )
        return permissions.toTypedArray()
    }
}

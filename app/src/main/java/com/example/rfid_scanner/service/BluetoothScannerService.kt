package com.example.rfid_scanner.service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.model.status.MConnectionStatus
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.utils.constant.Constant.DEVICE_NOT_CONNECTED
import com.example.rfid_scanner.utils.constant.Constant.DEVICE_TYPE_BLE
import com.example.rfid_scanner.utils.constant.Constant.DEVICE_TYPE_BTE
import com.rscja.deviceapi.RFIDWithUHFBLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class BluetoothScannerService(context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mInstance: BluetoothScannerService? = null

        fun getInstance() = mInstance!!

        fun init(context: Context) {
            mInstance = BluetoothScannerService(context)
        }
    }

    private val _sfScanStatus = MutableStateFlow(ScanStatus())
    val sfScanStatus = _sfScanStatus.asStateFlow()

    private val _sfStatus = MutableStateFlow(MConnectionStatus())
    val sfStatus = _sfStatus.asStateFlow()

    private val mBluetoothBLEService = BluetoothBLEService(CoroutineScope(Dispatchers.IO))
    private val mBluetoothBTEService = BluetoothBTEService(context, CoroutineScope(Dispatchers.IO))
    private val mUHFService = RFIDWithUHFBLE.getInstance()


    var connectedType : String = DEVICE_NOT_CONNECTED

    init {
        mUHFService.init(context)

        CoroutineScope(Dispatchers.IO).launch {
            launch {
                flowOf(
                    mBluetoothBLEService.sfStatus,
                    mBluetoothBTEService.sfStatus
                ).flattenMerge().collect {
                    if (it.isConnected) {
                        connectedType = if (mBluetoothBLEService.isConnected) DEVICE_TYPE_BLE
                        else DEVICE_TYPE_BTE
                    }
                    _sfStatus.value = it
                }
            }
            launch {
                flowOf(
                    mBluetoothBLEService.sfScanStatus,
                    mBluetoothBTEService.sfScanStatus
                ).flattenMerge().collect {
                    _sfScanStatus.value = it
                }
            }
        }
    }

    val isBluetoothEnabled
        get() = mBluetoothBTEService.isBluetoothEnabled

    val isConnected
        get() =
            if (connectedType == DEVICE_TYPE_BLE) mBluetoothBLEService.isConnected
            else mBluetoothBTEService.isConnected

    val currentDevice
        get() =
            if (connectedType == DEVICE_TYPE_BLE) mBluetoothBLEService.currentDevice
            else mBluetoothBTEService.currentDevice

    fun enableBluetooth(mARLEnableBluetooth: ActivityResultLauncher<Intent?>) {
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        mARLEnableBluetooth.launch(enableIntent)
    }

    fun connectBluetooth(deviceAddress: String, deviceType: String) {
        if (deviceType == DEVICE_TYPE_BTE) mBluetoothBTEService.makeConnection(deviceAddress)
        else mUHFService.connect(deviceAddress, mBluetoothBLEService)
    }

    fun disconnectBluetooth() {
        if (connectedType == DEVICE_TYPE_BTE) mBluetoothBTEService.closeConnection()
        else mUHFService.disconnect()
        connectedType = DEVICE_NOT_CONNECTED
    }

    fun startScan() {
        if (connectedType == DEVICE_TYPE_BTE) mBluetoothBTEService.startScan()
        else mBluetoothBLEService.startScan()
    }

    fun stopScan() {
        if (connectedType == DEVICE_TYPE_BTE) mBluetoothBTEService.stopScan()
        else mBluetoothBLEService.stopScan()
    }

    fun setChannel(channelTags: Channel<List<TagEPC>>) {
        mBluetoothBLEService.setChannel(channelTags)
        mBluetoothBTEService.setChannel(channelTags)
    }


}


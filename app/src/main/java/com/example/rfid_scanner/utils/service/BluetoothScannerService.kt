package com.example.rfid_scanner.utils.service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.model.status.MConnectionStatus
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.utils.constant.Constant.DEVICE_NOT_CONNECTED
import com.example.rfid_scanner.utils.constant.Constant.DEVICE_TYPE_BLE
import com.example.rfid_scanner.utils.constant.Constant.DEVICE_TYPE_BTE
import com.example.rfid_scanner.utils.generic.HandledEvent
import com.rscja.deviceapi.RFIDWithUHFBLE
import kotlinx.coroutines.CoroutineScope

class BluetoothScannerService(context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mInstance: BluetoothScannerService? = null

        fun getInstance() = mInstance!!

        fun init(context: Context, coroutineScope: CoroutineScope) {
            mInstance = BluetoothScannerService(context)
        }
    }

//    private val _ldTags = MutableLiveData<List<TagEPC>>()
//    val ldTags : LiveData<List<TagEPC>> = _ldTags

    private val _ldTags = MutableLiveData<HandledEvent<List<TagEPC>>>()
    val ldTags : LiveData<HandledEvent<List<TagEPC>>> = _ldTags

    private val _sfScanStatus = MutableLiveData<ScanStatus>()
    val sfScanStatus : LiveData<ScanStatus> = _sfScanStatus

    private val _sfStatus = MutableLiveData<MConnectionStatus>()
    val sfStatus : LiveData<MConnectionStatus> = _sfStatus

    private val mBluetoothBLEService = BluetoothBLEService()
    private val mBluetoothBTEService = BluetoothBTEService(context)
    private val mUHFService = RFIDWithUHFBLE.getInstance()


    var connectedType : String = DEVICE_NOT_CONNECTED

    init {
        mUHFService.init(context)

//        mBluetoothBLEService.ldTags.observeForever { _ldTags.postValue(it) }
        mBluetoothBTEService.ldTags.observeForever { _ldTags.postValue(it) }

        mBluetoothBLEService.sfStatus.observeForever {
            if (it.isConnected) connectedType = DEVICE_TYPE_BLE
            _sfStatus.postValue(it)
        }
        mBluetoothBTEService.sfStatus.observeForever {
            if (it.isConnected) connectedType = DEVICE_TYPE_BTE
            _sfStatus.value = it
        }

        mBluetoothBLEService.sfScanStatus.observeForever { _sfScanStatus.postValue(it) }
        mBluetoothBTEService.sfScanStatus.observeForever { _sfScanStatus.postValue(it) }
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


}


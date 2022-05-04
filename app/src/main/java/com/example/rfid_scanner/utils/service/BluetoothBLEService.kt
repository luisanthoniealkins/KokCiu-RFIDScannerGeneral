package com.example.rfid_scanner.utils.service

import android.bluetooth.BluetoothDevice
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.model.status.MConnectionStatus
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.rscja.deviceapi.RFIDWithUHFBLE
import com.rscja.deviceapi.entity.UHFTAGInfo
import com.rscja.deviceapi.interfaces.ConnectionStatus
import com.rscja.deviceapi.interfaces.ConnectionStatusCallback
import java.lang.Runnable

class BluetoothBLEService : ConnectionStatusCallback<Any?>{

    var currentDevice: BluetoothDevice? = null
        private set

    private val _ldTags = MutableLiveData<List<TagEPC>>()
    val ldTags : LiveData<List<TagEPC>> = _ldTags

    private val _sfScanStatus = MutableLiveData<ScanStatus>()
    val sfScanStatus : LiveData<ScanStatus> = _sfScanStatus

    private val _sfStatus = MutableLiveData<MConnectionStatus>()
    val sfStatus : LiveData<MConnectionStatus> = _sfStatus

    private val mUHFService = RFIDWithUHFBLE.getInstance()


    override fun getStatus(conStatus: ConnectionStatus, _device: Any?) {
        Runnable {
            if (conStatus == ConnectionStatus.CONNECTED) currentDevice = _device as BluetoothDevice?
            else if (conStatus == ConnectionStatus.DISCONNECTED) currentDevice = null
            updateStatus()
        }.run()
    }

    val isConnected: Boolean
        get() = mUHFService.connectStatus == ConnectionStatus.CONNECTED


    private var isScanning = false

    private fun updateStatus() {
        _sfStatus.postValue(MConnectionStatus(isConnected, false))
        updateScanStatus()
    }

    private fun updateScanStatus() {
        _sfScanStatus.postValue(ScanStatus(isConnected, isScanning, false))
    }

    fun startScan() {
        TagThread().start()
    }

    fun stopScan() {
        isScanning = false
        updateScanStatus()
        mUHFService.stopInventory()
    }

    inner class TagThread : Thread() {
        override fun run() {
            isScanning = mUHFService.startInventoryTag()

            updateScanStatus()
            while (isScanning) {
                val list: List<UHFTAGInfo>? = mUHFService.readTagFromBufferList()
                if (list.isNullOrEmpty()) {
                    SystemClock.sleep(1)
                    continue
                }
                _ldTags.postValue(list.map { TagEPC(it.epc) })
            }
        }
    }

}
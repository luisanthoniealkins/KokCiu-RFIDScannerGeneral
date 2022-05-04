package com.example.rfid_scanner.utils.service

import android.bluetooth.BluetoothDevice
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.model.status.MConnectionStatus
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.utils.generic.HandleEvent
import com.rscja.deviceapi.RFIDWithUHFBLE
import com.rscja.deviceapi.entity.UHFTAGInfo
import com.rscja.deviceapi.interfaces.ConnectionStatus
import com.rscja.deviceapi.interfaces.ConnectionStatusCallback
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.Runnable

class BluetoothBLEService(private val coroutineScope: CoroutineScope): ConnectionStatusCallback<Any?>{

    var currentDevice: BluetoothDevice? = null
        private set

    private val _ldTags = MutableLiveData<HandleEvent<List<TagEPC>>>()
    val ldTags : LiveData<HandleEvent<List<TagEPC>>> = _ldTags

    private val _sfStatus = MutableStateFlow(MConnectionStatus())
    val sfStatus : StateFlow<MConnectionStatus> = _sfStatus

    private val _sfScanStatus = MutableStateFlow(ScanStatus())
    val sfScanStatus : StateFlow<ScanStatus> = _sfScanStatus

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
        _sfStatus.value = MConnectionStatus(isConnected, false)
        updateScanStatus()
    }

    private fun updateScanStatus() {
        _sfScanStatus.value = ScanStatus(isConnected, isScanning, false)
    }

    fun startScan() {
        coroutineScope.launch {
            isScanning = mUHFService.startInventoryTag()

            updateScanStatus()
            while (isScanning) {
                val list: List<UHFTAGInfo>? = mUHFService.readTagFromBufferList()
                if (list.isNullOrEmpty()) {
                    SystemClock.sleep(1)
                    continue
                }
                _ldTags.postValue(HandleEvent(list.map { TagEPC(it.epc) }))
            }
        }

    }

    fun stopScan() {
        mUHFService.stopInventory()
        isScanning = false
        updateScanStatus()
    }

}
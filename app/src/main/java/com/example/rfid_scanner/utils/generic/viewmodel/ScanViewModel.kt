package com.example.rfid_scanner.utils.generic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.service.BluetoothScannerService
import kotlinx.coroutines.launch

abstract class ScanViewModel : BaseViewModel() {

    val mBluetoothScannerService = BluetoothScannerService.getInstance()

    private val _scanStatus = MutableLiveData<ScanStatus>()
    val scanStatus : LiveData<ScanStatus> = _scanStatus

    init {
        viewModelScope.launch {
            launch { mBluetoothScannerService.sfScanStatus.collect{ _scanStatus.postValue(it) } }
        }
    }

    open fun resetTags() {}

}

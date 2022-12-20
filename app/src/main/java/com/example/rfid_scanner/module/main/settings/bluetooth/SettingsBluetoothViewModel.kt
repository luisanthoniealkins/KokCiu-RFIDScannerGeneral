package com.example.rfid_scanner.module.main.settings.bluetooth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel

class SettingsBluetoothViewModel : BaseViewModel() {

    private val _macAddress = MutableLiveData<String>().apply { postValue(StorageService.getI().printerMacAddress) }
    val macAddress : LiveData<String> = _macAddress

    fun updateSP(macAddress: String) {
        StorageService.getI().printerMacAddress = macAddress
    }


}
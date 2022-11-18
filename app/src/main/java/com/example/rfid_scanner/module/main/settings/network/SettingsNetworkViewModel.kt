package com.example.rfid_scanner.module.main.settings.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel

class SettingsNetworkViewModel : BaseViewModel() {

    private val _ipAddress = MutableLiveData<String>().apply { postValue(StorageService.getI().ipAddress) }
    val ipAddress : LiveData<String> = _ipAddress

    private val _port = MutableLiveData<String>().apply { postValue(StorageService.getI().port) }
    val port : LiveData<String> = _port

    private val _wifi = MutableLiveData<String>().apply { postValue(StorageService.getI().wifi) }
    val wifi : LiveData<String> = _wifi

    fun updateSP(ipAddress: String, port: String, wifi: String) {
        StorageService.getI().ipAddress = ipAddress
        StorageService.getI().port = port
        StorageService.getI().wifi = wifi
    }


}
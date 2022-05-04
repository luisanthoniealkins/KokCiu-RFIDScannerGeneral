package com.example.rfid_scanner.module.main.setting.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rfid_scanner.utils.generic.BaseViewModel
import com.example.rfid_scanner.utils.service.StorageService

class NetworkSettingViewModel : BaseViewModel() {

    private val _ipAddress = MutableLiveData<String>().apply { postValue(StorageService.getInstance().ipAddress) }
    val ipAddress : LiveData<String> = _ipAddress

    private val _port = MutableLiveData<String>().apply { postValue(StorageService.getInstance().port) }
    val port : LiveData<String> = _port

    private val _wifi = MutableLiveData<String>().apply { postValue(StorageService.getInstance().wifi) }
    val wifi : LiveData<String> = _wifi

    fun updateSP(ipAddress: String, port: String, wifi: String) {
        StorageService.getInstance().ipAddress = ipAddress
        StorageService.getInstance().port = port
        StorageService.getInstance().wifi = wifi
    }


}
package com.example.rfid_scanner.module.main.settings.general

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel

class SettingsGeneralViewModel : BaseViewModel() {

    private val _lvQrCodeDelimiter = MutableLiveData<String>().apply { postValue(StorageService.getI().qrCodeDelimiter) }
    val lvQrCodeDelimiter : LiveData<String> = _lvQrCodeDelimiter

    fun updateSP(qrCodeDelimiter: String) {
        StorageService.getI().qrCodeDelimiter = qrCodeDelimiter
    }


}
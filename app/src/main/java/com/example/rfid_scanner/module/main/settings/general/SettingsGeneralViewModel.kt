package com.example.rfid_scanner.module.main.settings.general

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel

class SettingsGeneralViewModel : BaseViewModel() {

    private val _lvQrCodeDelimiter = MutableLiveData<String>().apply { postValue(StorageService.getI().qrCodeDelimiter) }
    val lvQrCodeDelimiter : LiveData<String> = _lvQrCodeDelimiter

    private val _lvTransactionHistoryQueryLimit = MutableLiveData<Int>().apply { postValue(StorageService.getI().transactionHistoryQueryLimit) }
    val lvTransactionHistoryQueryLimit : LiveData<Int> = _lvTransactionHistoryQueryLimit

    private val _lvStockIdQueryLimit = MutableLiveData<Int>().apply { postValue(StorageService.getI().stockIdQueryLimit) }
    val lvStockIdQueryLimit : LiveData<Int> = _lvStockIdQueryLimit

    fun updateSP(qrCodeDelimiter: String, stockIdQueryLimit: Int, transactionHistoryQueryLimit: Int) {
        StorageService.getI().qrCodeDelimiter = qrCodeDelimiter
        StorageService.getI().stockIdQueryLimit = stockIdQueryLimit
        StorageService.getI().transactionHistoryQueryLimit = transactionHistoryQueryLimit
    }


}
package com.example.rfid_scanner.utils.generic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laalkins.bluetoothgeneralcontroller.utils.custom.kclass.HandledEvent

abstract class BaseViewModel : ViewModel() {

    private val _lvToastMessage = MutableLiveData<HandledEvent<String>>()
    val lvToastMessage : LiveData<HandledEvent<String>> = _lvToastMessage

    protected fun showToast(message: String) {
        _lvToastMessage.postValue(HandledEvent(message))
    }
}

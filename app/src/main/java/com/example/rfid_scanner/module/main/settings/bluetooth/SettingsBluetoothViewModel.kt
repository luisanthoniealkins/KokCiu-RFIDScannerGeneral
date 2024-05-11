package com.example.rfid_scanner.module.main.settings.bluetooth

import android.util.Log
import com.example.rfid_scanner.data.model.BTDeviceConfig
import com.example.rfid_scanner.module.main.settings.bluetooth.adapter.BTDeviceConfigViewHolder
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.service.StorageService.Companion.storage
import com.example.rfid_scanner.utils.constant.Constant.BluetoothDeviceType.*
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel

class SettingsBluetoothViewModel : BaseViewModel() {


    val deviceAdapter = BTDeviceConfigViewHolder(mutableListOf()).getAdapter()

    init {
        refreshDeviceList()
    }

    private fun refreshDeviceList() {
        deviceAdapter.clearData()
        storage.btDeviceConfigs.reversed().map {
            deviceAdapter.addData(it)
            Log.d("12345", "${it.macAddress} ${it.deviceType} ${it.prefixCodeCut} ${it.suffixCodeCut}")
        }
    }

    fun addDeviceConfig(btDeviceConfig: BTDeviceConfig) {
        storage.btDeviceConfigs.add(btDeviceConfig)
        storage.saveBtDeviceConfigs()
        refreshDeviceList()
    }

    fun updateDeviceConfig(btDeviceConfig: BTDeviceConfig) {
        storage.btDeviceConfigs[
                storage.btDeviceConfigs.indexOfFirst { it.macAddress == btDeviceConfig.macAddress }
        ] = btDeviceConfig
        storage.saveBtDeviceConfigs()
        refreshDeviceList()
    }

    fun deleteDeviceConfig(btDeviceConfig: BTDeviceConfig) {
        storage.btDeviceConfigs.removeIf { it.macAddress == btDeviceConfig.macAddress }
        storage.saveBtDeviceConfigs()
        refreshDeviceList()
    }

//    private val _printerMacAddress = MutableLiveData<String>().apply { postValue(StorageService.getI().printerMacAddress) }
//    val printerMacAddress : LiveData<String> = _printerMacAddress
//
//    fun updateSP(macAddress: String) {
//        StorageService.getI().printerMacAddress = macAddress
//    }
}
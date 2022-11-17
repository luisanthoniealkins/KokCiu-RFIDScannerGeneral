package com.example.rfid_scanner.utils.listener

interface DeviceSelectedListener {
    fun onDeviceSelected(deviceAddress: String, deviceType: String);
}
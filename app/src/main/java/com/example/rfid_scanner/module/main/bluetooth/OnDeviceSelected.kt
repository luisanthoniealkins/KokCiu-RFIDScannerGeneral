package com.example.rfid_scanner.module.main.bluetooth

interface OnDeviceSelected {
    fun onDeviceSelected(deviceAddress: String, deviceType: String);
}
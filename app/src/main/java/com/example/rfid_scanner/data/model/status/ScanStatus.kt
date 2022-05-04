package com.example.rfid_scanner.data.model.status

data class ScanStatus(val isConnected: Boolean = false, val isScanning: Boolean = false, val isPressing: Boolean = false)
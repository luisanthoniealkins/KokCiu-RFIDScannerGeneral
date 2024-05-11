package com.example.rfid_scanner.data.model

import com.example.rfid_scanner.utils.constant.Constant.BluetoothDeviceType
import com.example.rfid_scanner.utils.constant.Constant.BluetoothDeviceType.ScannerLongRange

data class BTDeviceConfig(
    val macAddress: String = "00:00:00:00:00:00",
    val deviceType: BluetoothDeviceType = ScannerLongRange,

    val prefixCodeCut: Int = defaultPrefixCodeCut(),
    val suffixCodeCut: Int = defaultSuffixCodeCut(),
) {
    companion object {
        fun defaultPrefixCodeCut() = 0
        fun defaultSuffixCodeCut() = 0
    }
}
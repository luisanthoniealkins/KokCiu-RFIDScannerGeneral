package com.example.rfid_scanner.data.model.status

data class BluetoothStatus (

    var status: String,

    var statusColor: Int,

    var deviceName: String,

    var deviceAddress: String,

    var batteryLevel: Float,

    var message: String

)
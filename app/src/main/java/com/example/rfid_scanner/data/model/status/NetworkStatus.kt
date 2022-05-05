package com.example.rfid_scanner.data.model.status

data class NetworkStatus (

    var status: String,

    var statusColor: Int,

    var ssid: String,

    var strength: Int,

    var message: String,

    var toastMessage: String?,

)
package com.example.rfid_scanner.utils.constant

object Constant {

    const val APP_VERSION = "1.1 Unstable"

    const val DEVICE_NOT_CONNECTED = "no_connection"
    const val DEVICE_TYPE_BTE = "bte" // short range
    const val DEVICE_TYPE_BLE = "ble" // long range

    const val SERVICE_STATUS_OK = "OK"
    const val SERVICE_STATUS_ERROR = "Error"

    const val BTE_START_SCAN_COMMAND = "!O"
    const val BTE_STOP_SCAN_COMMAND = "!C"

    const val BUTTON_SCAN_TEXT = "Scan"
    const val BUTTON_STOP_TEXT = "Stop"

    const val PASSWORD_ADMIN = "admin22"
    const val PASSWORD_SUPER_ADMIN = "s22"

    const val PROPERTY_TYPE_CUSTOMER = 0
    const val PROPERTY_TYPE_BRAND = 1
    const val PROPERTY_TYPE_VEHICLE_TYPE = 2
    const val PROPERTY_TYPE_UNIT = 3

    enum class Users {
        Basic,
        BasicAdmin,
        MasterAdmin,
    }
}
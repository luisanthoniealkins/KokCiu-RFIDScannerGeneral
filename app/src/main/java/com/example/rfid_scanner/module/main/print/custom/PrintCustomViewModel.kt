package com.example.rfid_scanner.module.main.print.custom

import android.os.Handler
import android.os.Looper
import com.example.rfid_scanner.service.BluetoothScannerService
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.constant.Constant.DEVICE_TYPE_BTE
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import com.example.rfid_scanner.utils.helper.TextHelper.emptyString

class PrintCustomViewModel : BaseViewModel() {

    val mBluetoothScannerService = BluetoothScannerService.getInstance()

    private var previousConnectedAddress = emptyString()
    private var previousConnectedType = emptyString()

    init {
        previousConnectedAddress = mBluetoothScannerService.currentDevice?.address ?: emptyString()
        previousConnectedType = mBluetoothScannerService.connectedType

        mBluetoothScannerService.disconnectBluetooth()
        showToast("Connecting to printer bluetooth")

        Handler(Looper.getMainLooper()).postDelayed({
            mBluetoothScannerService.connectBluetooth(StorageService.getI().printerMacAddress!!, DEVICE_TYPE_BTE)
        }, 1000)
    }

    fun print(text: String) {
        mBluetoothScannerService.sendCustomMessage(text)
    }

    fun reconnectPreviousBluetooth() {
        mBluetoothScannerService.disconnectBluetooth()
        if (previousConnectedAddress.isEmpty()) return

        showToast("Reconnecting to previous bluetooth")
        Handler(Looper.getMainLooper()).postDelayed({
            mBluetoothScannerService.connectBluetooth(previousConnectedAddress, previousConnectedType)
        }, 1000)
    }

}
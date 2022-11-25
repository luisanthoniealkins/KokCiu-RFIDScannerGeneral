package com.example.rfid_scanner.module.main.print.checkout

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.data.model.Bill
import com.example.rfid_scanner.service.BluetoothScannerService
import com.example.rfid_scanner.utils.constant.Constant.DEVICE_TYPE_BTE
import com.example.rfid_scanner.utils.constant.Constant.PRINTER_BT_ADDRESS
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import com.example.rfid_scanner.utils.helper.TextHelper.emptyString

class PrintCheckoutViewModel : BaseViewModel() {

    val mBluetoothScannerService = BluetoothScannerService.getInstance()

    private val _lvTaskFinished = MutableLiveData<Boolean>()
    val lvTaskFinished: LiveData<Boolean> = _lvTaskFinished

    private var previousConnectedAddress = emptyString()
    private var previousConnectedType = emptyString()

    private var bills = listOf<Bill>()

    init {
        previousConnectedAddress = mBluetoothScannerService.currentDevice?.address ?: emptyString()
        previousConnectedType = mBluetoothScannerService.connectedType

        mBluetoothScannerService.disconnectBluetooth()
        showToast("Connecting to printer bluetooth")

        Handler(Looper.getMainLooper()).postDelayed({
            mBluetoothScannerService.connectBluetooth(PRINTER_BT_ADDRESS, DEVICE_TYPE_BTE)
        }, 1000)
    }

    fun reconnectPreviousBluetooth() {
        mBluetoothScannerService.disconnectBluetooth()
        if (previousConnectedAddress.isEmpty()) return

        showToast("Reconnecting to previous bluetooth")
        Handler(Looper.getMainLooper()).postDelayed({
            mBluetoothScannerService.connectBluetooth(previousConnectedAddress, previousConnectedType)
        }, 1000)
    }

    fun printWithFormat(packageCount: Int) {
        val footer = "\n--------------------------------\n\n"
        val str = getPrintFormat("NAMA: ", listOf(bills.first().customerName)) +
                getPrintFormat("VIA : ", listOf(bills.first().delivery)) +
                "\n" +
                getPrintFormat("KOLI: ", listOf("$packageCount")) +
                getPrintFormat("NO  : ", bills.map { it.billCode }) +
                footer

        repeat(packageCount) { mBluetoothScannerService.sendCustomMessage(str) }
        _lvTaskFinished.postValue(true)
    }

    private fun getPrintFormat(headerType: String, content: List<String>): String{
        var completeText = ""
        var line = headerType
        content.map {
            it.map { c ->
                if (line.length == 32) {
                    completeText += line + "\n"
                    line = emptyString()
                    repeat(headerType.length) { line += " " }
                }
                if (!(line.last() == ' ' && c == ' ')) line += c
            }
            if (line.isNotEmpty()) {
                completeText += line + "\n"
                line = emptyString()
                repeat(headerType.length) { line += " " }
            }
        }
        return completeText
    }

    fun setBills(bills: List<Bill>) {
        this.bills = bills
    }

}
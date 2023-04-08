package com.example.rfid_scanner.module.main.print.checkout

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.Bill
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.constant.Constant.DEVICE_TYPE_BTE
import com.example.rfid_scanner.utils.generic.viewmodel.ScanViewModel
import com.example.rfid_scanner.utils.helper.TextHelper.emptyString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PrintCheckoutViewModel : ScanViewModel() {

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
            mBluetoothScannerService.connectBluetooth(StorageService.getI().printerMacAddress!!, DEVICE_TYPE_BTE)
        }, 500)
    }

    fun reconnectPreviousBluetooth() {
        mBluetoothScannerService.disconnectBluetooth()
        if (previousConnectedAddress.isEmpty()) return

        showToast("Reconnecting to previous bluetooth")
        Handler(Looper.getMainLooper()).postDelayed({
            mBluetoothScannerService.connectBluetooth(previousConnectedAddress, previousConnectedType)
        }, 500)
    }

    fun printWithFormat(packageCount: Int) {
        val footer = "\n--------------------------------\n\n"
        val footer2 = "\n\n\n\n--------------------------------\n\n"

        val str = getPrintFormat("NAMA: ", listOf(bills.first().customerName)) +
                getPrintFormat("VIA : ", listOf(bills.first().delivery)) +
                "\n" +
                getPrintFormat("KOLI: ", listOf("$packageCount")) +
                getPrintFormat("NO  : ", bills.map { it.billCode }) +
                if (packageCount < 3) footer2
                else footer

        viewModelScope.launch {
            repeat(packageCount) {
                mBluetoothScannerService.sendCustomMessage(str)
                delay(500)
            }
            _lvTaskFinished.postValue(true)
        }
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
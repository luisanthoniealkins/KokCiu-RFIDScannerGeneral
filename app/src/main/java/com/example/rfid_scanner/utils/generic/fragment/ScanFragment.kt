package com.example.rfid_scanner.utils.generic.fragment

import android.widget.Button
import androidx.viewbinding.ViewBinding
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.utils.generic.BaseViewModel

abstract class ScanFragment<VBinding : ViewBinding, ViewModel : BaseViewModel> : BaseFragment<VBinding, ViewModel>() {

    /**
     * Customization start here
     * */

    abstract fun getScanButton() : Button
    abstract fun getOtherButton() : List<Button>

    fun updateUIButton(status: ScanStatus) {
        getOtherButton().map { it.isEnabled = !status.isScanning }

        with(getScanButton()) {
            when {
                status.isConnected && !status.isScanning -> {
                    isEnabled = true
                    text = ("Scan")
                }
                status.isConnected && status.isScanning && !status.isPressing -> {
                    isEnabled = true
                    text = ("Stop")
                }
                else -> isEnabled = false
            }
        }
    }

}
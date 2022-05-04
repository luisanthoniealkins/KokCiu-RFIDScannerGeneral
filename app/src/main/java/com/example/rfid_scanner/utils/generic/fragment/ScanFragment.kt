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
    abstract fun getStopButton() : Button
    abstract fun getOtherButton() : List<Button>

    fun updateUIButton(status: ScanStatus) {
        getOtherButton().map { it.isEnabled = !status.isScanning }
        getScanButton().isEnabled = status.isConnected && !status.isScanning
        getStopButton().isEnabled = status.isConnected && status.isScanning && !status.isPressing
    }

}
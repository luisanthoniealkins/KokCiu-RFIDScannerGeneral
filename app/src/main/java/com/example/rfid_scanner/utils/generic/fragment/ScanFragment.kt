package com.example.rfid_scanner.utils.generic.fragment

import android.widget.Button
import androidx.viewbinding.ViewBinding
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.utils.generic.viewmodel.ScanViewModel

abstract class ScanFragment<VBinding : ViewBinding, ViewModel : ScanViewModel> : BaseFragment<VBinding, ViewModel>() {

    /**
     * Customization start here
     * */
    abstract fun getScanButton() : Button
    abstract fun getResetButton() : Button
    abstract fun getNonScanButtons() : List<Button>

    override fun onPause() {
        super.onPause()
        viewModel.mBluetoothScannerService.stopScan()
    }

    fun updateUIButton(status: ScanStatus) {
        getNonScanButtons().map { it.isEnabled = !status.isScanning }
        if (!getResetButton().hasOnClickListeners()) {
            getResetButton().setOnClickListener { viewModel.resetTags() }
        }

        with(getScanButton()) {
            when {
                status.isConnected && !status.isScanning -> {
                    isEnabled = true
                    text = "Scan"
                    setOnClickListener { viewModel.mBluetoothScannerService.startScan() }
                }
                status.isConnected && status.isScanning && !status.isPressing -> {
                    isEnabled = true
                    text = "Stop"
                    setOnClickListener { viewModel.mBluetoothScannerService.stopScan() }
                }
                else -> isEnabled = false
            }
        }
    }



}
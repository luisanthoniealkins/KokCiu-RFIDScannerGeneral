package com.example.rfid_scanner.utils.generic.bottom_sheet

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.viewbinding.ViewBinding
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.utils.generic.viewmodel.ScanViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class ScanBottomSheet<VBinding : ViewBinding, ViewModel : ScanViewModel> : BaseBottomSheet<VBinding, ViewModel>() {

    /**
     * Customization start here
     * */
    abstract fun getScanButton() : Button
    abstract fun getResetButton() : Button
    abstract fun getNonScanButtons() : List<Button>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext())

        dialog.setContentView(binding.root)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { bottomSheet ->
                val behaviour = BottomSheetBehavior.from(bottomSheet)
                val layoutParams = bottomSheet.layoutParams
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                bottomSheet.layoutParams = layoutParams
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }

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

    override fun observeData() {
        viewModel.scanStatus.observeWithOwner {

        }
    }

}
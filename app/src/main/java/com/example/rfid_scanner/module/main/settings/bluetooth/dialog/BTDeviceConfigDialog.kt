package com.example.rfid_scanner.module.main.settings.bluetooth.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.BTDeviceConfig
import com.example.rfid_scanner.databinding.DialogBtDeviceConfigBinding
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.service.StorageService.Companion.storage
import com.example.rfid_scanner.utils.constant.Constant.BluetoothDeviceType.*
import com.example.rfid_scanner.utils.constant.Constant.DialogResult.*
import com.example.rfid_scanner.utils.constant.Constant.REGEX_MAC_ADDRESS
import com.example.rfid_scanner.utils.generic.dialog.BaseDialog
import com.example.rfid_scanner.utils.listener.DialogConfirmationListener

class BTDeviceConfigDialog(
    private val macAddress: String?,
    private val listener: DialogConfirmationListener,
) : BaseDialog<DialogBtDeviceConfigBinding>() {

    val isCreate = macAddress.isNullOrEmpty()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogBtDeviceConfigBinding.inflate(inflater, container, false)

    override fun setUpViews() = with(binding) {

        tvTitle.text = if (isCreate) "Tambah Perangkat" else "Edit Perangkat"
        imvDelete.visibility = if (isCreate) View.GONE else View.VISIBLE
        imvDelete.setOnClickListener {
            showConfirmationDialogAndDismiss {
                listener.onDialogDismiss(ACTION_DELETE, BTDeviceConfig(macAddress!!))
            }
        }

        if (!isCreate) {
            StorageService.getI().btDeviceConfigs.firstOrNull { it.macAddress == macAddress}?.let {
                edtMacAddress.isEnabled = false
                edtMacAddress.setTextColor(gColor(R.color.dark_gray_item_text_disable))
                edtMacAddress.setText(it.macAddress)

                tbDeviceType.check(
                    when(it.deviceType){
                        ScannerLongRange -> R.id.btn_device_type_scanner_far
                        ScannerShortRange -> R.id.btn_device_type_scanner_close
                        Printer -> R.id.btn_device_type_printer
                    }
                )

                edtPrefixCodeCut.setText(it.prefixCodeCut.toString())
                edtSuffixCodeCut.setText(it.suffixCodeCut.toString())
            }
        }

        btnCancel.setOnClickListener { dismiss() }
        btnOk.setOnClickListener { validateAndConfirmInput() }
    }

    private fun validateAndConfirmInput() {
        val macAddress = binding.edtMacAddress.text.toString().trim().takeIf { it.matches(Regex(REGEX_MAC_ADDRESS)) }

        val deviceType = when(binding.tbDeviceType.checkedButtonId) {
            binding.btnDeviceTypeScannerFar.id -> ScannerLongRange
            binding.btnDeviceTypeScannerClose.id -> ScannerShortRange
            else -> Printer
        }

        val prefixCodeCut = binding.edtPrefixCodeCut.text.toString().trim().toIntOrNull()
        val suffixCodeCut = binding.edtSuffixCodeCut.text.toString().trim().toIntOrNull()

        binding.tilMacAddress.error =
            if (macAddress == null) "Format MAC Address tidak sesuai"
            else if (isCreate && storage.btDeviceConfigs.any { it.macAddress == macAddress }) "MAC Address sudah terdaftar"
            else ""
        binding.tilPrefixCodeCut.error = if (prefixCodeCut == null) "Format angka tidak sesuai" else ""
        binding.tilSuffixCodeCut.error = if (suffixCodeCut == null) "Format angka tidak sesuai" else ""

        if (!binding.tilMacAddress.error.isNullOrEmpty() ||
            !binding.tilPrefixCodeCut.error.isNullOrEmpty() ||
            !binding.tilSuffixCodeCut.error.isNullOrEmpty()
        ) return

        showConfirmationDialogAndDismiss {
            listener.onDialogDismiss(
                if (isCreate) ACTION_ADD else ACTION_UPDATE,
                BTDeviceConfig(
                    macAddress!!,
                    deviceType,
                    prefixCodeCut!!.toInt(),
                    suffixCodeCut!!.toInt()
                )
            )
        }
    }

    private fun showConfirmationDialogAndDismiss(func : () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi")
            .setMessage("Apakah anda yakin untuk menjalankan operasi?")
            .setPositiveButton("Ok") { _, _ ->
                func()
                dismiss()
            }
            .setNegativeButton("Batal") { _, _ -> }
            .create()
            .show()
    }

}
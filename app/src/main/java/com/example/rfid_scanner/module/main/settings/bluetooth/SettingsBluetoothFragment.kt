package com.example.rfid_scanner.module.main.settings.bluetooth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.rfid_scanner.databinding.FragmentSettingsBluetoothBinding
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class SettingsBluetoothFragment : BaseFragment<FragmentSettingsBluetoothBinding, SettingsBluetoothViewModel>(){

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSettingsBluetoothBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = SettingsBluetoothViewModel::class.java

    override fun setUpViews() = with(binding) {
        imvBack.setOnClickListener { navigateBack() }
        btnConfirm.setOnClickListener { validateAndConfirmInput() }
    }

    override fun observeData() = with(viewModel) {
        macAddress.observeWithOwner { binding.edtPrinterAddress.setText(it) }
    }

    private fun validateAndConfirmInput() {
        val macAddress = binding.edtPrinterAddress.text.toString().trim()

        var isError = false
        binding.tilPrinterAddress.error =
            if (macAddress.isNotEmpty()) ""
            else {
                isError = true
                "IP Address harus diisi"
            }

        if (isError) return

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi")
            .setMessage("Apakah anda yakin untuk menjalankan operasi?")
            .setPositiveButton("Ok") { _, _ ->
                viewModel.updateSP(macAddress)
                navigateBack()
            }
            .setNegativeButton("Batal") { _, _ -> }
            .create()
            .show()
    }
}
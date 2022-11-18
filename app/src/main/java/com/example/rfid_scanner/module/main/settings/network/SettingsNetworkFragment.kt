package com.example.rfid_scanner.module.main.settings.network

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.rfid_scanner.databinding.FragmentSettingsNetworkBinding
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class SettingsNetworkFragment : BaseFragment<FragmentSettingsNetworkBinding, SettingsNetworkViewModel>(){

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSettingsNetworkBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = SettingsNetworkViewModel::class.java

    override fun setUpViews() = with(binding) {
        imvBack.setOnClickListener { navigateBack() }
        btnConfirm.setOnClickListener { validateAndConfirmInput() }
    }


    override fun observeData() = with(viewModel) {
        ipAddress.observeWithOwner { binding.edtIpAddress.setText(it) }
        port.observeWithOwner { binding.edtPort.setText(it) }
        wifi.observeWithOwner { binding.edtWifiName.setText(it) }
    }


    private fun validateAndConfirmInput() {
        val ipAddress = binding.edtIpAddress.text.toString().trim()
        val port = binding.edtPort.text.toString().trim()
        val wifi = binding.edtWifiName.text.toString().trim()

        var isError = false
        binding.tilIpAddress.error =
            if (ipAddress.isNotEmpty()) ""
            else {
                isError = true
                "IP Address harus diisi"
            }

        binding.tilPort.error =
            if (port.isNotEmpty()) ""
            else {
                isError = true
                "Port harus diisi"
            }

        binding.tilWifiName.error =
            if (wifi.isNotEmpty()) ""
            else {
                isError = true
                "Nama Wifi harus diisi"
            }

        if (isError) return

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi")
            .setMessage("Apakah anda yakin untuk menjalankan operasi?")
            .setPositiveButton("Ok") { _, _ ->
                viewModel.updateSP(ipAddress, port, wifi)
                navigateBack()
            }
            .setNegativeButton("Batal") { _, _ -> }
            .create()
            .show()
    }



}
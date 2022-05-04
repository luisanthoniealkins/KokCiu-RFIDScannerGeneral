package com.example.rfid_scanner.module.main.setting.network

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import com.example.rfid_scanner.databinding.FragmentNetworkSettingBinding
import com.example.rfid_scanner.utils.generic.BaseFragment

class NetworkSettingFragment : BaseFragment<FragmentNetworkSettingBinding, NetworkSettingViewModel>(){

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentNetworkSettingBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = NetworkSettingViewModel::class.java

    override fun setUpViews() {
        with(binding) {
            imvBack.setOnClickListener { view?.findNavController()?.popBackStack() }
            btnConfirm.setOnClickListener { validateAndConfirmInput() }
        }
    }

    override fun observeData() {
        Log.d("12345-", viewModel.ipAddress.value.toString())
        with(viewModel) {
            ipAddress.observe(viewLifecycleOwner, {binding.edtIpAddress.setText(it)})
            port.observe(viewLifecycleOwner, {binding.edtPort.setText(it)})
            wifi.observe(viewLifecycleOwner, {binding.edtWifiName.setText(it)})
        }
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

        val builder = AlertDialog.Builder(context!!)
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
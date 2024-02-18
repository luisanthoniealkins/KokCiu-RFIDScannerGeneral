package com.example.rfid_scanner.module.main.settings.general

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.rfid_scanner.databinding.FragmentSettingsGeneralBinding
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class SettingsGeneralFragment : BaseFragment<FragmentSettingsGeneralBinding, SettingsGeneralViewModel>(){

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSettingsGeneralBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = SettingsGeneralViewModel::class.java

    override fun setUpViews() = with(binding) {
        imvBack.setOnClickListener { navigateBack() }
        btnConfirm.setOnClickListener { validateAndConfirmInput() }
    }

    override fun observeData() = with(viewModel) {
        lvQrCodeDelimiter.observeWithOwner { binding.edtQrCodeDelimiter.setText(it) }
    }

    private fun validateAndConfirmInput() {
        val qrCodeDelimiter = binding.edtQrCodeDelimiter.text.toString().trim()

        var isError = false
        binding.tilQrCodeDelimiter.error =
            if (qrCodeDelimiter.isNotEmpty()) ""
            else {
                isError = true
                "Pembagi harus diisi"
            }

        if (isError) return

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi")
            .setMessage("Apakah anda yakin untuk menjalankan operasi?")
            .setPositiveButton("Ok") { _, _ ->
                viewModel.updateSP(qrCodeDelimiter)
                navigateBack()
            }
            .setNegativeButton("Batal") { _, _ -> }
            .create()
            .show()
    }



}
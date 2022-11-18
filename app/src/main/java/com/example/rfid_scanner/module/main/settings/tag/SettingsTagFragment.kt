package com.example.rfid_scanner.module.main.settings.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.rfid_scanner.databinding.FragmentSettingsTagBinding
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class SettingsTagFragment : BaseFragment<FragmentSettingsTagBinding, SettingsTagViewModel>(){

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSettingsTagBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = SettingsTagViewModel::class.java

    override fun setUpViews() = with(binding) {
        imvBack.setOnClickListener { navigateBack() }
        btnConfirm.setOnClickListener { validateAndConfirmInput() }
    }

    override fun observeData() = with(viewModel) {
        lvMinEPCLength.observeWithOwner { binding.edtMinEpc.setText(it.toString()) }
        lvMaxEPCLength.observeWithOwner { binding.edtMaxEpc.setText(it.toString()) }
        lvEPCDiffTolerance.observeWithOwner { binding.edtToleranceTagDiff.setText(it.toString()) }
    }

    private fun validateAndConfirmInput() {
        val minEPCLength = binding.edtMinEpc.text.toString().trim()
        val maxEPCLength = binding.edtMaxEpc.text.toString().trim()
        val epcDiffTolerance = binding.edtToleranceTagDiff.text.toString().trim()

        var isError = false
        binding.tilMinEpc.error =
            if (minEPCLength.isNotEmpty()) ""
            else {
                isError = true
                "IP Address harus diisi"
            }

        binding.tilMaxEpc.error =
            if (maxEPCLength.isNotEmpty()) ""
            else {
                isError = true
                "Port harus diisi"
            }

        binding.tilToleranceTagDiff.error =
            if (epcDiffTolerance.isNotEmpty()) ""
            else {
                isError = true
                "Nama Wifi harus diisi"
            }

        if (isError) return

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi")
            .setMessage("Apakah anda yakin untuk menjalankan operasi?")
            .setPositiveButton("Ok") { _, _ ->
                viewModel.updateSP(minEPCLength.toInt(), maxEPCLength.toInt(), epcDiffTolerance.toInt())
                navigateBack()
            }
            .setNegativeButton("Batal") { _, _ -> }
            .create()
            .show()
    }



}
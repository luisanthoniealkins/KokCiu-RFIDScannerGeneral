package com.example.rfid_scanner.module.main.settings.general

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.rfid_scanner.databinding.FragmentSettingsGeneralBinding
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment
import com.example.rfid_scanner.utils.helper.InputVerificationHelper.mustFilledInput
import com.example.rfid_scanner.utils.helper.InputVerificationHelper.mustNumberInput
import com.example.rfid_scanner.utils.helper.InputVerificationHelper.mustSmallNumberInput
import com.example.rfid_scanner.utils.helper.InputVerificationHelper.verifyInput

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
        lvStockIdQueryLimit.observeWithOwner { binding.edtStockIdQueryLimit.setText(it.toString()) }
        lvTransactionHistoryQueryLimit.observeWithOwner { binding.edtTransactionHistoryQueryLimit.setText(it.toString()) }
    }

    private fun validateAndConfirmInput() {
        val isError = listOf(
            verifyInput(
                binding.edtQrCodeDelimiter, binding.tilQrCodeDelimiter,
                listOf(::mustFilledInput)
            ),
            verifyInput(
                binding.edtStockIdQueryLimit, binding.tilStockIdQueryLimit,
                listOf(::mustFilledInput, ::mustNumberInput, ::mustSmallNumberInput)
            ),
            verifyInput(
                binding.edtTransactionHistoryQueryLimit, binding.tilTransactionHistoryQueryLimit,
                listOf(::mustFilledInput, ::mustNumberInput, ::mustSmallNumberInput)
            ),
        ).contains(false)

        if (isError) return

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi")
            .setMessage("Apakah anda yakin untuk menjalankan operasi?")
            .setPositiveButton("Ok") { _, _ ->
                viewModel.updateSP(
                    binding.edtQrCodeDelimiter.text.toString().trim(),
                    binding.edtStockIdQueryLimit.text.toString().trim().toInt(),
                    binding.edtTransactionHistoryQueryLimit.text.toString().trim().toInt(),
                )
                navigateBack()
            }
            .setNegativeButton("Batal") { _, _ -> }
            .create()
            .show()
    }

}
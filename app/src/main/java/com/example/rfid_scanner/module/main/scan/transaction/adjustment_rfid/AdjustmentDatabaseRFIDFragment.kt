package com.example.rfid_scanner.module.main.scan.transaction.adjustment_rfid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.databinding.FragmentAdjustmentDatabaseRfidBinding
import com.example.rfid_scanner.utils.generic.fragment.ScanFragment

class AdjustmentDatabaseRFIDFragment : ScanFragment<FragmentAdjustmentDatabaseRfidBinding, AdjustmentDatabaseRFIDViewModel>() {

    override fun getViewModelClass() = AdjustmentDatabaseRFIDViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAdjustmentDatabaseRfidBinding.inflate(inflater, container, false)

    override fun getScanButton() = binding.btnScan
    override fun getResetButton() = binding.btnReset
    override fun getNonScanButtons() = listOf(binding.btnReset, binding.btnCommit)

    override fun retrieveArgs() {
        val args : AdjustmentDatabaseRFIDFragmentArgs by navArgs()

        binding.edtCode.setText(args.stockCode)
        binding.edtName.setText(args.stockName)
        binding.edtUnitCount.setText(args.stockUnitCount.toString())
    }

    override fun setUpViews() = with(binding) {
        edtCode.setTextColor(gColor(R.color.dark_gray_item_text_disable))
        edtName.setTextColor(gColor(R.color.dark_gray_item_text_disable))
        edtUnitCount.setTextColor(gColor(R.color.dark_gray_item_text_disable))
        edtRfid.setTextColor(gColor(R.color.dark_gray_item_text_disable))

        btnCommit.setOnClickListener { verifyInput() }
    }

    override fun observeData() = with(viewModel) {
        lvLatestEPC.observeWithOwner { binding.edtRfid.setText(it) }

        commitState.observeWithOwner {
            binding.btnCommit.isEnabled = (it != MResponse.LOADING)
            if (it == MResponse.FINISHED_SUCCESS) {
                showToast("Transaksi berhasil")
                navigateBack()
            }
        }

        scanStatus.observeWithOwner { updateUIButton(it) }
    }

    private fun verifyInput() {
        val rfid = binding.edtRfid.text.toString()

        var isError = false
        binding.tilRfid.error =
            if (rfid.isNotEmpty()) ""
            else {
                isError = true
                "RFID perlu di scan terlebih dahulu"
            }
        if (isError) return

        showConfirmationDialog(
            "Konfirmasi",
            "Apakah anda yakin untuk menjalankan transaksi?"
        ) {
            viewModel.commit(binding.edtCode.text.toString(), binding.edtUnitCount.text.toString().toInt(), rfid)
        }
    }
}
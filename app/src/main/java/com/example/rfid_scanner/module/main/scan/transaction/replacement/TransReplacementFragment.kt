package com.example.rfid_scanner.module.main.scan.transaction.replacement

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.databinding.FragmentTransReplacementBinding
import com.example.rfid_scanner.module.main.scan.transaction.replacement.TransReplacementViewModel.Companion.TAB_ERROR
import com.example.rfid_scanner.module.main.scan.transaction.replacement.TransReplacementViewModel.Companion.TAB_TAG
import com.example.rfid_scanner.module.main.scan.transaction.replacement.verify.VerifyBottomSheet
import com.example.rfid_scanner.utils.generic.fragment.ScanFragment
import com.google.android.material.tabs.TabLayout

class TransReplacementFragment : ScanFragment<FragmentTransReplacementBinding, TransReplacementViewModel>() {

    override fun getViewModelClass() = TransReplacementViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTransReplacementBinding.inflate(inflater, container, false)

    override fun getScanButton() = binding.btnScan
    override fun getResetButton() = binding.btnReset
    override fun getNonScanButtons() = listOf(binding.btnReset, binding.btnVerifyAndCommit)

    override fun retrieveArgs() {
        val args: TransReplacementFragmentArgs by navArgs()

        binding.tvCode.text = args.stockCode
        binding.tvName.text = args.stockName
        binding.tvEpc.text = args.tagEPC

        viewModel.setReplacementData(
            args.stockCode,
            Tag(
                epc = args.tagEPC,
                stockId = args.tagStockId,
                stockUnitCount = args.tagStockUnitCount
            )
        )
    }

    override fun setUpViews() = with(binding) {
        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.replacementTagAdapter
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    TAB_TAG -> rvItem.adapter = viewModel.replacementTagAdapter
                    TAB_ERROR -> rvItem.adapter = viewModel.errorAdapter
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun observeData() = with(viewModel) {
        tagCountOK.observeWithOwner { binding.tabLayout.getTabAt(TAB_TAG)?.text = "Tag Mirip / Sama (${it})" }
        tagCountError.observeWithOwner { binding.tabLayout.getTabAt(TAB_ERROR)?.text = "Tag Lain (${it})" }

        scanStatus.observeWithOwner { updateUIButton(it) }

        isVerified.observeWithOwner {
            binding.btnVerifyAndCommit.text =
                if (it) "Ganti"
                else "Verifikasi"

            binding.btnVerifyAndCommit.setOnClickListener {
                if (isVerified.value == true) showConfirmationDialog()
                else verifyTags()
            }
        }

        commitState.observeWithOwner {
            binding.btnVerifyAndCommit.isEnabled = (it != MResponse.LOADING)
            if (it == MResponse.FINISHED_SUCCESS) {
                showToast("Transaksi berhasil")
                navigateBack()
            }
        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi")
            .setMessage("Apakah anda yakin untuk menjalankan transaksi?")
            .setPositiveButton("Ok") { _, _ -> viewModel.commitTransaction()}
            .setNegativeButton("Batal") { _, _ -> }
            .create()
            .show()
    }

    private fun verifyTags() {
        viewModel.replacementTagAdapter.getErrorMessage()?.let {
            showToast(it)
            return
        }

        val error = viewModel.errorAdapter.getError()
        if (!error.isNullOrEmpty()) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Warning")
                .setMessage("Terdapat beberapa error. Apakah anda yakin untuk menjalankan verifikasi?")
                .setPositiveButton("Ok") { _, _ -> verifySimilarTags() }
                .setNegativeButton("Batal") { _, _ -> }
                .create()
                .show()
        } else {
            verifySimilarTags()
        }
    }

    private fun verifySimilarTags() {
        val error3 = viewModel.checkSimilarTags()
        if (error3.isNotEmpty()) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Potensi Kode Duplikat")
                .setMessage(error3)
                .setNegativeButton("Batal") { _, _ -> }
                .create()
                .show()
        } else {
            showVerifyBottomSheet()
        }
    }

    private fun showVerifyBottomSheet() {
        VerifyBottomSheet(viewModel, viewModel.replacementTagAdapter.getScannedTags())
            .show(childFragmentManager, "Bottom Sheet Dialog Fragment")
    }

    override fun onPause() {
        super.onPause()
        viewModel.mBluetoothScannerService.stopScan()
    }

}
package com.example.rfid_scanner.module.main.scan.transaction.adjustment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.databinding.FragmentTransAdjustmentBinding
import com.example.rfid_scanner.module.main.data.explore.stock.ExploreStockViewModel
import com.example.rfid_scanner.module.main.scan.transaction.adjustment.TransAdjustmentViewModel.Companion.TAB_ERROR
import com.example.rfid_scanner.module.main.scan.transaction.adjustment.TransAdjustmentViewModel.Companion.TAB_TAG
import com.example.rfid_scanner.module.main.scan.transaction.general.verify.VerifyBottomSheet
import com.example.rfid_scanner.utils.generic.fragment.ScanFragment
import com.google.android.material.tabs.TabLayout

class TransAdjustmentFragment : ScanFragment<FragmentTransAdjustmentBinding, TransAdjustmentViewModel>() {

    override fun getViewModelClass() = TransAdjustmentViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTransAdjustmentBinding.inflate(inflater, container, false)

    override fun getScanButton() = binding.btnScan
    override fun getResetButton() = binding.btnReset
    override fun getNonScanButtons() = listOf(binding.btnReset, binding.btnVerifyAndCommit)

    override fun retrieveArgs() {
        getNavController()?.currentBackStackEntry?.savedStateHandle?.getLiveData<List<String>?>(
            ExploreStockViewModel.KEY_STOCK)?.observeWithOwner {
                if (it == null) {
                    navigateBack()
                } else {
                    binding.tvCode.text = it[0]
                    binding.tvName.text = it[1]
                    viewModel.setStockCode(it[0])
                }
        }
    }

    override fun setUpViews() = with(binding) {

        cbShowOk.setOnCheckedChangeListener { _, b -> viewModel.setShowingOK(b) }

        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.adjustmentTagAdapter
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    TAB_TAG -> rvItem.adapter = viewModel.adjustmentTagAdapter
                    TAB_ERROR -> rvItem.adapter = viewModel.errorAdapter
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun observeData() = with(viewModel) {
        tagCountOK.observeWithOwner { binding.tabLayout.getTabAt(TAB_TAG)?.text = "Tag (${it})" }
        tagCountError.observeWithOwner { binding.tabLayout.getTabAt(TAB_ERROR)?.text = "Error (${it})" }

        scanStatus.observeWithOwner { updateUIButton(it) }

        isVerified.observeWithOwner {
            binding.btnVerifyAndCommit.text =
                if (it) "Hapus"
                else "Verif"

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

    override fun initEvent() {
        if (viewModel.isInitialEntry) {
            viewModel.isInitialEntry = false
            navigateTo(TransAdjustmentFragmentDirections.toExploreStockFragment(true))
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
                .setPositiveButton("Ok") { _, _ -> showVerifyBottomSheet() }
                .setNegativeButton("Batal") { _, _ -> }
                .create()
                .show()
        } else {
            showVerifyBottomSheet()
        }
    }

    private fun showVerifyBottomSheet() {
        VerifyBottomSheet(viewModel, viewModel.adjustmentTagAdapter.getScannedTags(), true)
            .show(childFragmentManager, "Bottom Sheet Dialog Fragment")
    }

    override fun onPause() {
        super.onPause()
        viewModel.mBluetoothScannerService.stopScan()
    }

}
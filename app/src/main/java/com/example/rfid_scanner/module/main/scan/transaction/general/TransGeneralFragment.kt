package com.example.rfid_scanner.module.main.scan.transaction.general

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_BROKEN
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_LOST
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_SOLD
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_STORED
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_UNKNOWN
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.databinding.DialogStatusTypeBinding
import com.example.rfid_scanner.databinding.FragmentTransGeneralBinding
import com.example.rfid_scanner.module.main.data.explore.stockId.ExploreStockIdViewModel
import com.example.rfid_scanner.module.main.scan.transaction.general.TransGeneralViewModel.Companion.TAB_ERROR
import com.example.rfid_scanner.module.main.scan.transaction.general.TransGeneralViewModel.Companion.TAB_TAG
import com.example.rfid_scanner.module.main.scan.transaction.general.TransGeneralViewModel.Companion.GENERAL
import com.example.rfid_scanner.module.main.scan.transaction.general.verify.VerifyBottomSheet
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.constant.Constant
import com.example.rfid_scanner.utils.constant.Constant.BUTTON_SCAN_TEXT
import com.example.rfid_scanner.utils.generic.fragment.ScanFragment
import com.example.rfid_scanner.utils.helper.TagHelper
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class TransGeneralFragment : ScanFragment<FragmentTransGeneralBinding, TransGeneralViewModel>() {

    companion object {
        val mapOfCommitButton = mapOf(
            STATUS_STORED to "Masuk",
            STATUS_SOLD to "Keluar",
            STATUS_BROKEN to "Rusak",
            STATUS_LOST to "Hilang",
            STATUS_UNKNOWN to "Hapus",
        )
    }

    override fun getViewModelClass() = TransGeneralViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTransGeneralBinding.inflate(inflater, container, false)

    override fun getScanButton() = binding.btnScan
    override fun getResetButton() = binding.btnReset
    override fun getNonScanButtons() = listOf(binding.btnReset, binding.btnVerifyAndCommit, binding.btnSelectStock)

    override fun setUpViews() = with(binding) {
        getNavController()?.currentBackStackEntry?.savedStateHandle?.getLiveData<List<String>>(
            ExploreStockIdViewModel.KEY_STOCK_ID)?.observeWithOwner {
            viewModel.selectStockId(StockId.getStockIdFromId(it[0]))
            tvId.text = it[0]
            tvName.text = it[1]
            StorageService.getI().lastUsedStockId = it[0]
            StorageService.getI().lastUsedStockName = it[1]
        }

        imvInsertLast.setOnClickListener {
            viewModel.selectStockId(StockId.getStockIdFromId(StorageService.getI().lastUsedStockId!!))
            tvId.text = StorageService.getI().lastUsedStockId
            tvName.text = StorageService.getI().lastUsedStockName
        }

        btnSelectStock.setOnClickListener {
            navigateTo(TransGeneralFragmentDirections.toExploreStockIdFragment2(true))
        }

        btnStatusFrom.setOnClickListener { showChoiceDialog(true) }
        btnStatusTo.setOnClickListener { showChoiceDialog(false) }

        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.tagAdapter
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    TAB_TAG -> rvItem.adapter = viewModel.tagAdapter
                    TAB_ERROR -> rvItem.adapter = viewModel.errorAdapter
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        btnReset.setOnClickListener { viewModel.clearTags() }
    }

    override fun observeData() = with(viewModel) {
        statusFrom.observeWithOwner {
            binding.btnStatusFrom.text = it
            binding.llCheckIn.isVisible = (statusFrom.value == STATUS_UNKNOWN && statusTo.value == STATUS_STORED)
        }
        statusTo.observeWithOwner {
            binding.btnStatusTo.text = it
            binding.btnVerifyAndCommit.text =
                if (isVerified.value == true) mapOfCommitButton[it]
                else "Verif"
            binding.llCheckIn.isVisible = (statusFrom.value == STATUS_UNKNOWN && statusTo.value == STATUS_STORED)
        }
        allowTrans.observeWithOwner {
            binding.imvAllowTrans.setImageResource(
                if (it) R.drawable.ic_baseline_arrow_forward_ios_24
                else R.drawable.ic_baseline_do_not_disturb_24
            )
        }

        tagCountOK.observeWithOwner { binding.tabLayout.getTabAt(TAB_TAG)?.text = "Tag (${it})" }
        tagCountError.observeWithOwner { binding.tabLayout.getTabAt(TAB_ERROR)?.text = "Error (${it})" }

        scanStatus.observeWithOwner { updateUIButton(it) }
        isVerified.observeWithOwner {
            binding.btnVerifyAndCommit.text =
                if (isVerified.value == true) mapOfCommitButton[statusTo.value]
                else "Verif"

            binding.btnVerifyAndCommit.setOnClickListener {
                if (isVerified.value == true)  showConfirmationDialog()
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

    override fun retrieveArgs() {
        val args : TransGeneralFragmentArgs by navArgs()
        if (args.transactionType != GENERAL) {
            binding.llTransition.visibility = View.GONE
            binding.tvTransType.text = args.transactionType
        } else {
            binding.tvTransType.visibility = View.GONE
        }
        viewModel.setTransaction(args.transactionType)
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
        if (viewModel.allowTrans.value != true) {
            showToast("Transisi status tidak diperbolehkan")
            return
        }

        if (viewModel.statusFrom.value == STATUS_UNKNOWN && viewModel.statusTo.value == STATUS_STORED) {
            if (viewModel.stockId == null) {
                showToast("Barang harus dipilih")
                return
            }
        }

        val error1 = viewModel.tagAdapter.getError()
        val error2 = viewModel.errorAdapter.getError()
        listOf(error1, error2).map { it?.let {
            showToast(it)
            return
        }}

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
        VerifyBottomSheet(viewModel, viewModel.mapOfTags.map { it.value }, false)
            .show(childFragmentManager, TagHelper.TAG_BOTTOM_SHEET)
    }

    @SuppressLint("InflateParams")
    private fun showChoiceDialog(isSource: Boolean) {
        val dialog = Dialog(requireContext())

        val inflater = LayoutInflater.from(context)
        val sBinding = DialogStatusTypeBinding.inflate(inflater)

        with(sBinding) {
            btnStored.setOnClickListener {
                viewModel.setStatusButton(isSource, STATUS_STORED)
                dialog.dismiss()
            }
            btnSold.setOnClickListener {
                viewModel.setStatusButton(isSource, STATUS_SOLD)
                dialog.dismiss()
            }
            btnBroken.setOnClickListener {
                viewModel.setStatusButton(isSource, STATUS_BROKEN)
                dialog.dismiss()
            }
            btnLost.setOnClickListener {
                viewModel.setStatusButton(isSource, STATUS_LOST)
                dialog.dismiss()
            }
            btnUnknown.setOnClickListener {
                viewModel.setStatusButton(isSource, STATUS_UNKNOWN)
                dialog.dismiss()
            }

            dialog.setContentView(sBinding.root)
        }

        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.mBluetoothScannerService.stopScan()
    }

}
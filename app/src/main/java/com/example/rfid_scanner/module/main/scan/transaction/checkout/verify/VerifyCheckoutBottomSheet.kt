package com.example.rfid_scanner.module.main.scan.transaction.checkout.verify

import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.data.model.StockRequirement
import com.example.rfid_scanner.databinding.BottomSheetVerifyCheckoutBinding
import com.example.rfid_scanner.module.main.scan.transaction.checkout.verify.VerifyCheckoutViewModel.Companion.TAB_ERROR
import com.example.rfid_scanner.module.main.scan.transaction.checkout.verify.VerifyCheckoutViewModel.Companion.TAB_STOCK
import com.example.rfid_scanner.module.main.scan.transaction.checkout.verify.VerifyCheckoutViewModel.Companion.TAB_TAG
import com.example.rfid_scanner.utils.generic.bottom_sheet.ScanBottomSheet
import com.example.rfid_scanner.utils.helper.LogHelper
import com.example.rfid_scanner.utils.listener.VerifyListener
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VerifyCheckoutBottomSheet(
    private val listener: VerifyListener,
    private val verifyStocks: List<StockRequirement>,
) : ScanBottomSheet<BottomSheetVerifyCheckoutBinding, VerifyCheckoutViewModel>() {

    override fun getViewModelClass() = VerifyCheckoutViewModel::class.java
    override fun getViewBinding() = BottomSheetVerifyCheckoutBinding.inflate(LayoutInflater.from(context))

    override fun getScanButton() = binding.btnScan
    override fun getResetButton() = binding.btnReset
    override fun getNonScanButtons() = listOf(binding.btnReset, binding.btnConfirm)

    override fun retrieveArgs() {
        viewModel.setStocks(verifyStocks)
    }

    override fun setUpViews(): Unit = with(binding){

        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.stockAdapter
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    TAB_STOCK -> rvItem.adapter = viewModel.stockAdapter
                    TAB_TAG -> rvItem.adapter = viewModel.tagAdapter
                    TAB_ERROR -> rvItem.adapter = viewModel.errorAdapter
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        cbShowOk.setOnCheckedChangeListener { _, b -> viewModel.setShowingOK(b) }
        btnConfirm.setOnClickListener {
            viewModel.getAdapterError()?.let {
                showToast(it)
                return@setOnClickListener
            }

            showToast("Tag berhasil diverifikasi")
            listener.onVerifyBottomSheetDismiss(true)
            dismiss()
        }
    }

    /***
     * WOIII INI GA ADA OBVERSE WITH OWNER KAHHH
     */

    override fun observeData(): Unit = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            launch {
                tagCountStock.asFlow().collectLatest {
                    binding.tabLayout.getTabAt(TAB_STOCK)?.text = "Barang (${it})"
                }
            }
            launch {
                tagCountTag.asFlow().collectLatest {
                    binding.tabLayout.getTabAt(TAB_TAG)?.text = "Tag (${it})"
                }
            }
            launch {
                tagCountError.asFlow().collectLatest {
                    binding.tabLayout.getTabAt(TAB_ERROR)?.text = "Error (${it})"
                }
            }
            launch { scanStatus.asFlow().collectLatest { updateUIButton(it) } }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onVerifyBottomSheetDismiss(false)
    }
}



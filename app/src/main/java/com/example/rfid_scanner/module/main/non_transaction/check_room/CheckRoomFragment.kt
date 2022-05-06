package com.example.rfid_scanner.module.main.non_transaction.check_room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.databinding.FragmentCheckRoomBinding
import com.example.rfid_scanner.module.main.transaction.general.TransGeneralViewModel
import com.example.rfid_scanner.utils.generic.fragment.ScanFragment
import com.google.android.material.tabs.TabLayout

class CheckRoomFragment : ScanFragment<FragmentCheckRoomBinding, CheckRoomViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCheckRoomBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = CheckRoomViewModel::class.java

    override fun getScanButton() = binding.btnScan

    override fun getOtherButton() = listOf( binding.btnReset )

    override fun setUpViews() = with(binding) {
        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.stockAdapter
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    TransGeneralViewModel.TAB_TAG -> rvItem.adapter = viewModel.stockAdapter
                    TransGeneralViewModel.TAB_ERROR -> rvItem.adapter = viewModel.errorAdapter
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        cbShowOk.setOnCheckedChangeListener { _, b ->
            viewModel.stockAdapter.setShowOK(b)
            viewModel.refreshCount()
        }
        cbShowZero.setOnCheckedChangeListener { _, b ->
            viewModel.stockAdapter.setShowZero(b)
            viewModel.refreshCount()
        }

        btnScan.setOnClickListener {
            if (btnScan.text == "Scan") viewModel.mBluetoothScannerService.startScan()
            else viewModel.mBluetoothScannerService.stopScan()
        }
        btnReset.setOnClickListener { viewModel.clearTags() }
    }

    override fun observeData() = with(viewModel) {
        stockCount.observeWithOwner { binding.tabLayout.getTabAt(TransGeneralViewModel.TAB_TAG)?.text = "Barang (${it})" }
        tagErrorCount.observeWithOwner { binding.tabLayout.getTabAt(TransGeneralViewModel.TAB_ERROR)?.text = "Error (${it})" }

        scanStatus.observeWithOwner { updateUIButton(it) }
    }

    override fun onPause() {
        super.onPause()
        viewModel.mBluetoothScannerService.stopScan()
    }
}
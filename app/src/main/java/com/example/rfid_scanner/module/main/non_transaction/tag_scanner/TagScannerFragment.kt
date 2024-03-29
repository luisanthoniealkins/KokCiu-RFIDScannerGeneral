package com.example.rfid_scanner.module.main.non_transaction.tag_scanner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.databinding.FragmentTagScannerBinding
import com.example.rfid_scanner.utils.generic.fragment.ScanFragment

class TagScannerFragment : ScanFragment<FragmentTagScannerBinding, TagScannerViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTagScannerBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = TagScannerViewModel::class.java

    override fun getScanButton() = binding.btnScan

    override fun getOtherButton() = listOf( binding.btnReset )

    override fun setUpViews() = with(binding) {
        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.adapter

        btnScan.setOnClickListener {
            if (btnScan.text == "Scan") viewModel.mBluetoothScannerService.startScan()
            else viewModel.mBluetoothScannerService.stopScan()
        }
        btnReset.setOnClickListener { viewModel.clearTags() }
    }

    override fun observeData() = with(viewModel) {
        tagCount.observe(viewLifecycleOwner, { binding.tvTagCount.text = ("$it tag") })

        scanStatus.observe(viewLifecycleOwner, { updateUIButton(it) })
    }

    override fun onPause() {
        super.onPause()
        viewModel.mBluetoothScannerService.stopScan()
    }



}
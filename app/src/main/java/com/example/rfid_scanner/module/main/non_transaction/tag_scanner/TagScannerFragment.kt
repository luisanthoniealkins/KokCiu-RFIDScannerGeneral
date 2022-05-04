package com.example.rfid_scanner.module.main.non_transaction.tag_scanner

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.databinding.FragmentTagScannerBinding
import com.example.rfid_scanner.utils.generic.BaseFragment

class TagScannerFragment : BaseFragment<FragmentTagScannerBinding, TagScannerViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTagScannerBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = TagScannerViewModel::class.java

    override fun setUpViews() = with(binding) {
        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.adapter

        btnScan.setOnClickListener { viewModel.mBluetoothScannerService.startScan() }
        btnStop.setOnClickListener { viewModel.mBluetoothScannerService.stopScan() }
        btnReset.setOnClickListener { viewModel.clearTags() }
    }

    override fun observeData() = with(viewModel) {
        Log.d("123456-", tagCount.hasObservers().toString())
        tagCount.observe(viewLifecycleOwner, { binding.tvTagCount.text = ("$it tag") })

        scanStatus.observe(viewLifecycleOwner, {
            binding.btnReset.isEnabled = !it.isScanning
            if (it.isConnected) {
                if (it.isScanning) {
                    binding.btnScan.isEnabled = false
                    binding.btnStop.isEnabled = !it.isPressing
                } else {
                    binding.btnScan.isEnabled = true
                    binding.btnStop.isEnabled = false
                }
            } else {
                binding.btnScan.isEnabled = false
                binding.btnStop.isEnabled = false
            }
        })
    }


}
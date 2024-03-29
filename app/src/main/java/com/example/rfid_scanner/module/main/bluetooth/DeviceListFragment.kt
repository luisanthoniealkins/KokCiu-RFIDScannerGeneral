package com.example.rfid_scanner.module.main.bluetooth

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.databinding.FragmentDeviceListBinding
import com.example.rfid_scanner.utils.constant.Constant.DEVICE_TYPE_BLE
import com.example.rfid_scanner.utils.constant.Constant.DEVICE_TYPE_BTE
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class DeviceListFragment : BaseFragment<FragmentDeviceListBinding, DeviceListViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentDeviceListBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = DeviceListViewModel::class.java

    private lateinit var deviceSelectedListener: OnDeviceSelected

    override fun setUpViews() = with(binding) {
        rvDeviceList.layoutManager = LinearLayoutManager(context)
        rvDeviceList.adapter = viewModel.adapter

        imvBack.setOnClickListener { navigateBack() }
    }


    override fun observeData() = with(viewModel) {
        isScanning.observe(viewLifecycleOwner, { isScanning ->
            binding.btnScan.text = if (isScanning) "Cancel" else "Scan"
            binding.btnScan.setOnClickListener {
                if (isScanning) navigateBack()
                else viewModel.scanLeDevice(true)
            }
        })

        selectedDevice.observe(viewLifecycleOwner, {
            deviceSelectedListener.onDeviceSelected(
                it.address,
                if (DeviceListViewModel.bleDeviceAddressList.contains(it.address)) DEVICE_TYPE_BLE
                else DEVICE_TYPE_BTE
            )
            navigateBack()
        })
    }


    override fun initEvent() {
        viewModel.scanLeDevice(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        deviceSelectedListener = context as OnDeviceSelected
    }

    override fun onPause() {
        super.onPause()
        viewModel.scanLeDevice(false)
    }
}
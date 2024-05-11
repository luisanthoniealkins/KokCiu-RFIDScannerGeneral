package com.example.rfid_scanner.module.main.settings.bluetooth

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.data.model.BTDeviceConfig
import com.example.rfid_scanner.databinding.FragmentSettingsBluetoothBinding
import com.example.rfid_scanner.module.main.settings.bluetooth.dialog.BTDeviceConfigDialog
import com.example.rfid_scanner.utils.constant.Constant
import com.example.rfid_scanner.utils.constant.Constant.DialogResult
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment
import com.example.rfid_scanner.utils.helper.TagHelper
import com.example.rfid_scanner.utils.listener.DialogConfirmationListener
import com.example.rfid_scanner.utils.listener.ItemClickListener

class SettingsBluetoothFragment : BaseFragment<FragmentSettingsBluetoothBinding, SettingsBluetoothViewModel>(), ItemClickListener,
    DialogConfirmationListener {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSettingsBluetoothBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = SettingsBluetoothViewModel::class.java

    override fun setUpViews() = with(binding) {
        imvBack.setOnClickListener { navigateBack() }

        imbAddDevice.setOnClickListener { showConfigDialog(null) }

        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.deviceAdapter
    }

    override fun observeData() = with(viewModel) {
        deviceAdapter.listener = this@SettingsBluetoothFragment
    }

    override fun onItemClick(item: Any) {
        showConfigDialog(item as String)
    }

    private fun showConfigDialog(macAddress: String?) {
        BTDeviceConfigDialog(macAddress,this@SettingsBluetoothFragment).show(requireActivity().supportFragmentManager, TagHelper.TAG_DIALOG)
    }

    override fun onDialogDismiss(result: DialogResult, item: Any?) {
        when(result) {
            DialogResult.ACTION_ADD -> viewModel.addDeviceConfig(item as BTDeviceConfig)
            DialogResult.ACTION_UPDATE -> viewModel.updateDeviceConfig(item as BTDeviceConfig)
            DialogResult.ACTION_DELETE -> viewModel.deleteDeviceConfig(item as BTDeviceConfig)
        }
    }
}
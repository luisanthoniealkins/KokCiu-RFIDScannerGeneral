package com.example.rfid_scanner.module.main.bluetooth

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.databinding.ItemBluetoothDeviceBinding
import com.example.rfid_scanner.service.StorageService.Companion.storage
import com.example.rfid_scanner.utils.constant.Constant
import com.example.rfid_scanner.utils.constant.Constant.BluetoothDeviceType.*
import com.example.rfid_scanner.utils.listener.ItemClickListener

class DeviceListAdapter(private val listener: ItemClickListener) : RecyclerView.Adapter<DeviceListAdapter.DeviceVH>() {

    private val dataList = mutableListOf<BluetoothDevice>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        DeviceVH(ItemBluetoothDeviceBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))

    override fun onBindViewHolder(holder: DeviceVH, position: Int) {
        holder.bind(dataList[position])
    }

    fun updateData(isCreate: Boolean, position: Int, data: BluetoothDevice) {
        if (isCreate) {
            dataList.add(data)
            notifyItemInserted(position)
        } else {
            dataList[position] = data
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = dataList.size

    inner class DeviceVH ( private val binding : ItemBluetoothDeviceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BluetoothDevice) {
            with(binding) {
                tvName.text = data.name
                tvAddress.text = data.address
                when (storage.btDeviceConfigs.find { it.macAddress == data.address }?.deviceType) {
                    ScannerLongRange -> {
                        tvType.text = ("BLE")
                        tvTypeRange.text = ("Jauh")
                    }
                    ScannerShortRange -> {
                        tvType.text = ("BTE")
                        tvTypeRange.text = ("Dekat")
                    }
                    Printer -> {
                        tvType.text = ("PRINTER")
                        tvTypeRange.text = ("Printer")
                    }
                    else -> {
                        tvType.text = ("BELUM TERDAFTAR")
                        tvTypeRange.text = ("Belum Terdaftar")
                    }
                }
                llViewHolder.setOnClickListener { listener.onItemClick(data) }
            }
        }
    }

    data class DeviceListData(val position: Int, var data: BluetoothDevice)
}
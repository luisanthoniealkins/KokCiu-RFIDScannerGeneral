package com.example.rfid_scanner.module.main.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.module.main.bluetooth.DeviceListAdapter.DeviceListData
import com.example.rfid_scanner.utils.listener.ItemClickListener
import com.rscja.deviceapi.RFIDWithUHFBLE

class DeviceListViewModel : BaseViewModel(), ItemClickListener {

    companion object {
//        const val SCAN_PERIOD: Long = 10000 // 10 seconds
        const val SCAN_PERIOD: Long = 5000 // 10 seconds

        val bteDeviceAddressList = setOf(
            "98:DA:B0:00:3D:59"
        )

        val bleDeviceAddressList = setOf(
            "FE:88:A5:83:64:98"
        )

    }

    val adapter = DeviceListAdapter(this)

    private val _selectedDevice = MutableLiveData<BluetoothDevice>()
    val selectedDevice : LiveData<BluetoothDevice> = _selectedDevice

    private val _isScanning = MutableLiveData<Boolean>()
    val isScanning : LiveData<Boolean> = _isScanning

    private val uhf: RFIDWithUHFBLE = RFIDWithUHFBLE.getInstance()
    private val mapOfDevice = mutableMapOf<BluetoothDevice, DeviceListData>()

    fun scanLeDevice(enable: Boolean) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            Handler(Looper.getMainLooper()).postDelayed({
                uhf.stopScanBTDevices()
                _isScanning.postValue(false)
            }, SCAN_PERIOD)

            _isScanning.postValue(true)
            uhf.startScanBTDevices { bluetoothDevice: BluetoothDevice, _: Int, _: ByteArray? ->
                addDevice(bluetoothDevice)
            }
        } else {
            _isScanning.postValue(false)
            uhf.stopScanBTDevices()
        }
    }

    private fun addDevice(device: BluetoothDevice) {
        val isCreate = !mapOfDevice.containsKey(device)
        if (!isCreate) return
        mapOfDevice.getOrPut(device) {
            DeviceListData(mapOfDevice.size, device)
        }

        mapOfDevice.getOrDefault(device, null)?.let { adapterItem ->
            adapter.updateData(isCreate, adapterItem.position, adapterItem.data)
        }
    }

    override fun onItemClick(item: Any) {
        _selectedDevice.postValue(item as BluetoothDevice)
    }


}
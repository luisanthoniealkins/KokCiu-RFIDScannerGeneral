package com.example.rfid_scanner.module.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.databinding.ActivityMainBinding
import com.example.rfid_scanner.databinding.BottomSheetStatusBinding
import com.example.rfid_scanner.module.main.bluetooth.OnDeviceSelected
import com.example.rfid_scanner.utils.constant.Constant.DEVICE_NOT_CONNECTED
import com.example.rfid_scanner.utils.helper.ToastHelper.Companion.showToast
import com.example.rfid_scanner.utils.service.BluetoothScannerService
import com.example.rfid_scanner.utils.service.NetworkService
import com.example.rfid_scanner.utils.service.StorageService
import com.google.android.material.bottomsheet.BottomSheetDialog


class MainActivity : AppCompatActivity(), OnDeviceSelected{

    private var _binding : ActivityMainBinding? = null
    val binding : ActivityMainBinding get() = _binding!!

    private lateinit var viewModel : MainViewModel

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContentView(binding.root)
        supportActionBar?.hide()

        with(binding) {
            navController = findNavController(R.id.nav_host_fragment_activity_main)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                cvDeviceStatus.isVisible = !MainViewModel.hideStatusDestination.contains(destination.id)
            }

            binding.cvDeviceStatus.setOnClickListener { showBottomSheetDetail() }
        }

        with(viewModel) {
            ldNetworkStatus.observeWithOwner {
                binding.tvWifiStatus.text = it.status
//                binding.tvWifiStatus.setTextColor(reColor(it.statusColor))
            }

            ldServerStatus.observeWithOwner {
                binding.tvServerStatus.text = it.status
//                binding.tvServerStatus.setTextColor(reColor(it.statusColor))
            }

            ldBluetoothStatus.observeWithOwner {
                binding.tvBluetoothStatus.text = it.status
//                binding.tvBluetoothStatus.setTextColor(reColor(it.statusColor))
            }
        }

        initServiceWithContext()
    }



    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopService()
    }

    @SuppressLint("InflateParams")
    private fun showBottomSheetDetail() {
        val dialog = BottomSheetDialog(this)

        val inflater = LayoutInflater.from(this)
        val sBinding = BottomSheetStatusBinding.inflate(inflater)


        with(sBinding) {
            viewModel.ldNetworkStatus.observeWithOwner {
                tvWifiStatus.text = it.status
//                tvWifiStatus.setTextColor(reColor(it.statusColor))
                tvWifiSsid.text = it.ssid
                tvWifiStrength.text = ("${it.strength}%")
                tvWifiMessage.text = it.message
            }

            viewModel.ldServerStatus.observeWithOwner {
                tvServerStatus.text = it.status
//                tvServerStatus.setTextColor(reColor(it.statusColor))
                tvServerMessage.text = it.message
            }

            viewModel.ldBluetoothStatus.observeWithOwner {
                tvBluetoothStatus.text = it.status
//                tvBluetoothStatus.setTextColor(reColor(it.statusColor))
                tvBluetoothDeviceName.text = it.deviceName
                tvBluetoothDeviceAddress.text = it.deviceAddress
                tvBluetoothDeviceBattery.text = ("%.2f%%").format(it.batteryLevel)
                tvBluetoothMessage.text = it.message
                if (viewModel.mBluetoothScannerService.connectedType == DEVICE_NOT_CONNECTED) {
                    btnConnect.setText(R.string.BTNConnect)
//                    btnConnect.setBackgroundColor(reColor(R.color.green_connect))
                    btnConnect.setOnClickListener {
                        if (viewModel.mBluetoothScannerService.isBluetoothEnabled) {
                            navController.navigate(R.id.deviceListFragment)
                            dialog.dismiss()
                        } else {
                            viewModel.mBluetoothScannerService.enableBluetooth(mARLEnableBluetooth)
                        }
                    }
                } else {
                    btnConnect.setText(R.string.BTNDisconnect)
//                    btnConnect.setBackgroundColor(reColor(R.color.red_disconnect))
                    btnConnect.setOnClickListener { viewModel.mBluetoothScannerService.disconnectBluetooth() }
                }
            }

            imvRefresh.setOnClickListener { viewModel.checkServer() }

            dialog.setContentView(root)
        }

        dialog.show()
    }

    private fun initServiceWithContext() {
        VolleyRepository.init(this)
        StorageService.init(this)
        NetworkService.init(this)
        BluetoothScannerService.init(this)

        viewModel.initService()
    }

    override fun onDeviceSelected(deviceAddress: String, deviceType: String) {
        viewModel.mBluetoothScannerService.connectBluetooth(deviceAddress, deviceType)
    }

    private var mARLEnableBluetooth = registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            showToast(applicationContext, "Bluetooth has turned on")
        } else {
            showToast(applicationContext, "Error while turning on bluetooth")
        }
    }

    fun <T> LiveData<T>.observeWithOwner(function: (T) -> Unit) {
        this.observe(this@MainActivity, function)
    }
}



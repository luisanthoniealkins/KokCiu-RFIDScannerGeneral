package com.example.rfid_scanner.module.main.non_transaction.tag_scanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.databinding.FragmentTagScannerBinding
import com.example.rfid_scanner.utils.generic.BaseFragment

class TagScannerFragment : Fragment() {

    private lateinit var viewModel: TagScannerViewModel
    private var _binding: FragmentTagScannerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this)[TagScannerViewModel::class.java]
        _binding = FragmentTagScannerBinding.inflate(layoutInflater)

        with(binding) {
            rvItem.layoutManager = LinearLayoutManager(context)
            rvItem.adapter = viewModel.adapter

            btnScan.setOnClickListener { viewModel.mBluetoothScannerService.startScan() }
            btnStop.setOnClickListener { viewModel.mBluetoothScannerService.stopScan() }
            btnReset.setOnClickListener { viewModel.clearTags() }
        }

        with(viewModel) {
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

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        viewModel.mBluetoothScannerService.stopScan()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
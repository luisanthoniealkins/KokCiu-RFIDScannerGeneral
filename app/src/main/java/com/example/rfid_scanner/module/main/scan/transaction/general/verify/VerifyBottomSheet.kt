package com.example.rfid_scanner.module.main.scan.transaction.general.verify

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.databinding.BottomSheetVerifyBinding
import com.example.rfid_scanner.module.main.scan.transaction.general.TransGeneralViewModel.Companion.TAB_ERROR
import com.example.rfid_scanner.module.main.scan.transaction.general.TransGeneralViewModel.Companion.TAB_TAG
import com.example.rfid_scanner.utils.listener.VerifyListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VerifyBottomSheet(private val listener: VerifyListener, private val verifyTags: List<Tag>) :
    BottomSheetDialogFragment() {

    private var _binding: BottomSheetVerifyBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: VerifyViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(context!!)

        _binding = BottomSheetVerifyBinding.inflate(LayoutInflater.from(context))
        viewModel = ViewModelProvider(this)[VerifyViewModel::class.java]

        dialog.setContentView(binding.root)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { bottomSheet ->
                val behaviour = BottomSheetBehavior.from(bottomSheet)
                val layoutParams = bottomSheet.layoutParams
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                bottomSheet.layoutParams = layoutParams
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        setUpViews()
        observeData()

        return dialog
    }

    private fun setUpViews() = with(binding){
        viewModel.setAdapter(verifyTags)

        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.verifyTagAdapter
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    TAB_TAG -> rvItem.adapter = viewModel.verifyTagAdapter
                    TAB_ERROR -> rvItem.adapter = viewModel.errorAdapter
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        cbShowOk.setOnCheckedChangeListener { _, b -> viewModel.verifyTagAdapter.setShowingOK(b) }
        btnScan.setOnClickListener {
            if (btnScan.text == "Scan") viewModel.mBluetoothScannerService.startScan()
            else viewModel.mBluetoothScannerService.stopScan()
        }
        btnReset.setOnClickListener { viewModel.clearTags() }

        btnConfirm.setOnClickListener {
            if (viewModel.errorAdapter.getError() == null && viewModel.verifyTagAdapter.isAllVerified()) {
                listener.onVerifyBottomSheetDismiss(true)
                showToast("Tag berhasil diverifikasi")
                dismiss()
            } else {
                showToast("Terdapat error pada tag verifikasi")
            }
        }
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted{
            launch {
                tagCountOK.asFlow().collectLatest {
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

    private fun updateUIButton(status: ScanStatus) {
        getOtherButton().map { it.isEnabled = !status.isScanning }

        with(getScanButton()) {
            when {
                status.isConnected && !status.isScanning -> {
                    isEnabled = true
                    text = ("Scan")
                }
                status.isConnected && status.isScanning && !status.isPressing -> {
                    isEnabled = true
                    text = ("Stop")
                }
                else -> isEnabled = false
            }
        }
    }

    private fun getScanButton() = binding.btnScan

    private fun getOtherButton() = listOf(binding.btnConfirm, binding.btnReset)

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.mBluetoothScannerService.stopScan()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onVerifyBottomSheetDismiss(false)
    }

}



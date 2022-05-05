package com.example.rfid_scanner.module.main.transaction.general

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_BROKEN
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_LOST
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_SOLD
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_STORED
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_UNKNOWN
import com.example.rfid_scanner.databinding.DialogStatusTypeBinding
import com.example.rfid_scanner.databinding.FragmentTransGeneralBinding
import com.example.rfid_scanner.module.main.transaction.general.TransGeneralViewModel.Companion.TAB_ERROR
import com.example.rfid_scanner.module.main.transaction.general.TransGeneralViewModel.Companion.TAB_TAG
import com.example.rfid_scanner.module.main.transaction.general.verify.VerifyBottomSheet
import com.example.rfid_scanner.utils.generic.fragment.ScanFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class TransGeneralFragment : ScanFragment<FragmentTransGeneralBinding, TransGeneralViewModel>() {

    companion object {
        val mapOfCommitButton = mapOf(
            STATUS_STORED to "Masuk",
            STATUS_SOLD to "Keluar",
            STATUS_BROKEN to "Rusak",
            STATUS_LOST to "Hilang",
            STATUS_UNKNOWN to "Hapus",
        )
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTransGeneralBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = TransGeneralViewModel::class.java

    override fun getScanButton() = binding.btnScan

    override fun getOtherButton() = listOf(binding.btnReset, binding.btnVerifyAndCommit)

    override fun setUpViews() = with(binding) {
        btnStatusFrom.setOnClickListener { showChoiceDialog(true) }
        btnStatusTo.setOnClickListener { showChoiceDialog(false) }

        viewModel.setAdapter(context)
        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.tagAdapter
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    TAB_TAG -> rvItem.adapter = viewModel.tagAdapter
                    TAB_ERROR -> rvItem.adapter = viewModel.errorAdapter
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        btnScan.setOnClickListener {
            if (btnScan.text == "Scan") viewModel.mBluetoothScannerService.startScan()
            else viewModel.mBluetoothScannerService.stopScan()
        }
        btnReset.setOnClickListener { viewModel.clearTags() }
    }

    override fun observeData() = with(viewModel) {
        statusFrom.observeWithOwner { binding.btnStatusFrom.text = it }
        statusTo.observeWithOwner {
            binding.btnStatusTo.text = it
            binding.btnVerifyAndCommit.text =
                if (isVerified.value == true) mapOfCommitButton[it]
                else "Verif"
        }

        tagCountOK.observeWithOwner { binding.tabLayout.getTabAt(TAB_TAG)?.text = "Tag (${it})" }
        tagCountError.observeWithOwner { binding.tabLayout.getTabAt(TAB_ERROR)?.text = "Error (${it})" }

        scanStatus.observeWithOwner { updateUIButton(it) }
        isVerified.observeWithOwner {
            binding.btnVerifyAndCommit.text =
                if (isVerified.value == true) mapOfCommitButton[statusTo.value]
                else "Verif"

            binding.btnVerifyAndCommit.setOnClickListener {
                if (isVerified.value == true) viewModel.commitTransaction()
                else verifyTags()
            }
        }
    }

    private fun verifyTags() {
        val error1 = viewModel.tagAdapter.getError()
        val error2 = viewModel.errorAdapter.getError()
        listOf(error1, error2).map { it?.let {
            showToast(it);
            return
        }}
        VerifyBottomSheet(viewModel, viewModel.mapOfTags.map { it.value })
            .show(childFragmentManager, "Bottom Sheet Dialog Fragment")
    }

    @SuppressLint("InflateParams")
    private fun showChoiceDialog(isSource: Boolean) {
        val dialog = Dialog(context!!)

        val inflater = LayoutInflater.from(context)
        val sBinding = DialogStatusTypeBinding.inflate(inflater)

        with(sBinding) {
            btnStored.setOnClickListener {
                viewModel.setStatusButton(isSource, STATUS_STORED)
                dialog.dismiss()
            }
            btnSold.setOnClickListener {
                viewModel.setStatusButton(isSource, STATUS_SOLD)
                dialog.dismiss()
            }
            btnBroken.setOnClickListener {
                viewModel.setStatusButton(isSource, STATUS_BROKEN)
                dialog.dismiss()
            }
            btnLost.setOnClickListener {
                viewModel.setStatusButton(isSource, STATUS_LOST)
                dialog.dismiss()
            }
            btnUnknown.setOnClickListener {
                viewModel.setStatusButton(isSource, STATUS_UNKNOWN)
                dialog.dismiss()
            }

            dialog.setContentView(sBinding.root)
        }

        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.mBluetoothScannerService.stopScan()
    }

}
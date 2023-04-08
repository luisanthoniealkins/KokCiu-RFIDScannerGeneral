package com.example.rfid_scanner.module.main.scan.transaction.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Bill
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.databinding.FragmentTransCheckoutBinding
import com.example.rfid_scanner.module.main.menu.MenuFragmentDirections
import com.example.rfid_scanner.module.main.scan.transaction.checkout.TransCheckoutViewModel.Companion.TAB_ERROR
import com.example.rfid_scanner.module.main.scan.transaction.checkout.TransCheckoutViewModel.Companion.TAB_STOCK
import com.example.rfid_scanner.module.main.scan.transaction.checkout.dialog.CheckoutConfirmationDialog
import com.example.rfid_scanner.module.main.scan.transaction.checkout.qr_reader.QRReaderViewModel
import com.example.rfid_scanner.module.main.scan.transaction.checkout.verify.VerifyCheckoutBottomSheet
import com.example.rfid_scanner.utils.generic.fragment.ScanFragment
import com.example.rfid_scanner.utils.helper.TagHelper
import com.example.rfid_scanner.utils.helper.TagHelper.TAG_DIALOG
import com.example.rfid_scanner.utils.helper.TextHelper.defaultEmptyString
import com.example.rfid_scanner.utils.helper.TextHelper.emptyString
import com.example.rfid_scanner.utils.listener.DialogConfirmationListener
import com.example.rfid_scanner.utils.listener.VerifyListener
import com.google.android.material.tabs.TabLayout

class TransCheckoutFragment : ScanFragment<FragmentTransCheckoutBinding, TransCheckoutViewModel>(),
    VerifyListener, DialogConfirmationListener {

    /** Binding fragment with view and viewmodel */
    override fun getViewModelClass() = TransCheckoutViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTransCheckoutBinding.inflate(inflater, container, false)

    override fun getScanButton() = binding.btnScan
    override fun getResetButton() = binding.btnReset
    override fun getNonScanButtons() = listOf(binding.btnReset, binding.btnVerifyAndCommit, binding.btnAddQrCode)

    override fun retrieveArgs() {
        getNavController()?.currentBackStackEntry?.savedStateHandle?.getLiveData<Bill?>(
            QRReaderViewModel.KEY_BILL)?.observeWithOwner {
            viewModel.addBill(it)
        }
    }

    override fun setUpViews() = with(binding) {
        btnAddQrCode.setOnClickListener {
            navigateTo(TransCheckoutFragmentDirections.toQRReaderFragment(
                viewModel.getBillCodes().toTypedArray(),
                viewModel.getCustomerCode()))
        }

        rvItem.layoutManager = LinearLayoutManager(requireContext())
        rvItem.adapter = viewModel.stockAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    TAB_STOCK -> rvItem.adapter = viewModel.stockAdapter
                    TAB_ERROR -> rvItem.adapter = viewModel.errorAdapter
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun observeData() = with(viewModel) {
        lvBills.observeWithOwner {
            if (it.isEmpty()) navigateBack()
            else {
                var billCodeText = ""
                it.map { bill ->
                    if (billCodeText.isEmpty()) billCodeText = bill.billCode
                    else billCodeText += ", ${bill.billCode}"
                }
                binding.tvBillsCode.text = billCodeText
                binding.tvCustomerName.text = it.firstOrNull()?.customerName
                binding.tvTagVia.text =
                    if (it.firstOrNull()?.delivery == emptyString()) defaultEmptyString
                    else it.firstOrNull()?.delivery
                binding.tvTagDate.text = it.firstOrNull()?.formattedDate
            }
        }

        scanStatus.observeWithOwner { updateUIButton(it) }

        tagCountStock.observeWithOwner { binding.tabLayout.getTabAt(TAB_STOCK)?.text = "Barang (${it})" }
        tagCountError.observeWithOwner {
            binding.tabLayout.getTabAt(TAB_ERROR)?.text = "Error (${it})"
            binding.tabLayout.setSelectedTabIndicatorColor(
                gColor(if (it > 0) R.color.tab_item_error else R.color.tab_item_normal)
            )
            binding.tabLayout.setTabTextColors(
                gColor(R.color.gray_default_text_color),
                gColor(if (it > 0) R.color.tab_item_error else R.color.tab_item_normal)
            )
        }

        isVerified.observeWithOwner {
            binding.btnVerifyAndCommit.text =
                if (it) "Keluar"
                else "Verifikasi"

            binding.btnVerifyAndCommit.setOnClickListener {
                if (isVerified.value == true) showConfirmationDialog()
                else verifyTags()
            }
        }

        commitState.observeWithOwner {
            binding.btnVerifyAndCommit.isEnabled = (it != MResponse.LOADING)
            if (it == MResponse.FINISHED_SUCCESS) {
                showToast("Transaksi berhasil")
                navigateBack()
                navigateTo(MenuFragmentDirections.toPrintCheckoutFragment(bills.toTypedArray()))
            }
        }
    }

    override fun initEvent() {
        if (viewModel.isInitialEntry) {
            viewModel.isInitialEntry = false
            navigateTo(TransCheckoutFragmentDirections.toQRReaderFragment(
                viewModel.getBillCodes().toTypedArray(),
                viewModel.getCustomerCode()))
        }
    }

    private fun showConfirmationDialog() {
        CheckoutConfirmationDialog(
            viewModel.getTagCount(),
            this@TransCheckoutFragment
        ).show(requireActivity().supportFragmentManager, TAG_DIALOG)
    }

    private fun verifyTags() {
        viewModel.getAdapterError()?.let {
            showToast(it)
            return
        }

        val error3 = viewModel.checkSimilarTags()
        if (error3.isNotEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Potensi Kode Duplikat")
                .setMessage(error3)
                .setPositiveButton("Ok") { _, _ -> showVerifyBottomSheet() }
                .setNegativeButton("Batal") { _, _ -> }
                .create()
                .show()
        } else {
            showVerifyBottomSheet()
        }
    }

    private fun showVerifyBottomSheet() {
        VerifyCheckoutBottomSheet(
            this@TransCheckoutFragment,
            viewModel.stockAdapter.dataSet
        ).show(requireActivity().supportFragmentManager, TagHelper.TAG_BOTTOM_SHEET)
    }


    override fun onVerifyBottomSheetDismiss(result: Boolean) {
        viewModel.submitVerifyResult(result)
    }

    override fun onDialogDismiss(result: Boolean) {
        if (result) viewModel.commitTransaction()
    }

}
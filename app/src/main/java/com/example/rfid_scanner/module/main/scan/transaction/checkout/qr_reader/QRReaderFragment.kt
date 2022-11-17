package com.example.rfid_scanner.module.main.scan.transaction.checkout.qr_reader

import android.graphics.PointF
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.navArgs
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.example.rfid_scanner.data.model.Bill
import com.example.rfid_scanner.databinding.FragmentQrReaderBinding
import com.example.rfid_scanner.module.main.data.explore.stockId.ExploreStockIdViewModel
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment
import com.example.rfid_scanner.utils.helper.TextHelper.emptyString

class QRReaderFragment : BaseFragment<FragmentQrReaderBinding, QRReaderViewModel>(), QRCodeReaderView.OnQRCodeReadListener {

    /** Binding fragment with view and viewmodel */
    override fun getViewModelClass() = QRReaderViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentQrReaderBinding.inflate(inflater, container, false)

    override fun retrieveArgs() {
        val args : QRReaderFragmentArgs by navArgs()
        viewModel.registerBills(args.scannedBills, args.customerCode)
    }

    override fun setUpViews(): Unit = with(binding) {
        qrCodeReaderView.setOnQRCodeReadListener(this@QRReaderFragment)
        qrCodeReaderView.setQRDecodingEnabled(true)     // Use this function to enable/disable decoding
        qrCodeReaderView.setAutofocusInterval(2000L)    // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setTorchEnabled(true)          // Use this function to enable/disable Torch
        qrCodeReaderView.setFrontCamera()               // Use this function to set front camera preview
        qrCodeReaderView.setBackCamera()                // Use this function to set back camera preview

        btnRestartScan.setOnClickListener { startQRRead() }
        btnRestartScan.isEnabled = false

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getNavController()?.previousBackStackEntry?.savedStateHandle?.set(
                    QRReaderViewModel.KEY_BILL, null)
                navigateBack()
            }
        })
    }

    override fun observeData() = with(viewModel) {
        lvErrorMessage.observeWithOwner { error ->
            showToast(error.title)
            binding.tvError.text = error.title
            binding.btnRestartScan.isEnabled = true
            binding.imvError.visibility = View.VISIBLE
            binding.imvError.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(error.title)
                    .setMessage(error.detail)
                    .setNegativeButton("OK") { _, _ -> }
                    .show()
            }
        }

        lvConfiguredBill.observeWithOwner {
            getNavController()?.previousBackStackEntry?.savedStateHandle?.set(
                QRReaderViewModel.KEY_BILL, it)
            navigateBack()
        }
    }

    private fun startQRRead() = with(binding) {
        qrCodeReaderView.startCamera()
        qrCodeReaderView.setQRDecodingEnabled(true)
        btnRestartScan.isEnabled = false
        tvError.text = ""
        imvError.visibility = View.GONE
    }

    private fun stopQRRead() = with(binding) {
        qrCodeReaderView.stopCamera()
        qrCodeReaderView.setQRDecodingEnabled(false)
    }

    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {
        stopQRRead()
        val scannedBill = Bill(text ?: emptyString())
        viewModel.validateBarcode(scannedBill, text ?: emptyString())
    }

}
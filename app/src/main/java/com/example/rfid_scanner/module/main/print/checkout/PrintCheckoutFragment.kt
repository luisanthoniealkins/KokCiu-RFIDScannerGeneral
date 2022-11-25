package com.example.rfid_scanner.module.main.print.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import com.example.rfid_scanner.databinding.FragmentPrintCheckoutBinding
import com.example.rfid_scanner.utils.extension.StringExt.isNumberOnly
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class PrintCheckoutFragment : BaseFragment<FragmentPrintCheckoutBinding, PrintCheckoutViewModel>() {

    /** Binding fragment with view and viewmodel */
    override fun getViewModelClass() = PrintCheckoutViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPrintCheckoutBinding.inflate(inflater, container, false)

    override fun retrieveArgs() {
        val args: PrintCheckoutFragmentArgs by navArgs()

        viewModel.setBills(args.bills.toList())
    }

    override fun setUpViews(): Unit = with(binding) {
        btnPrint.setOnClickListener { verifyInput() }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showToast("Tekan cetak untuk keluar dari halaman ini")
            }
        })
    }

    override fun observeData() = with(viewModel){
        lvTaskFinished.observeWithOwner {
            if (it) {
                viewModel.reconnectPreviousBluetooth()
                navigateBack()
            }
        }
    }

    private fun verifyInput() {
        val kodiCount = binding.edtPackageCount.text.toString()

        var isError = false
        binding.tilPackageCount.error =
            if (kodiCount.isNotEmpty() && kodiCount.isNumberOnly()) ""
            else {
                isError = true
                "Jumlah koli harus diisi dan bertipe numerik"
            }

        if (isError) return

        viewModel.printWithFormat(kodiCount.toInt())
    }

}
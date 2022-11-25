package com.example.rfid_scanner.module.main.print.custom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.rfid_scanner.databinding.FragmentPrintCustomBinding
import com.example.rfid_scanner.module.main.data.explore.stock.ExploreStockViewModel
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class PrintCustomFragment : BaseFragment<FragmentPrintCustomBinding, PrintCustomViewModel>() {

    /** Binding fragment with view and viewmodel */
    override fun getViewModelClass() = PrintCustomViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPrintCustomBinding.inflate(inflater, container, false)

    override fun setUpViews(): Unit = with(binding) {
        btnPrint.setOnClickListener { viewModel.print(edtPrintMessage.text.toString()) }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.reconnectPreviousBluetooth()
                navigateBack()
            }
        })
    }

}
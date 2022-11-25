package com.example.rfid_scanner.module.template.fragment.base

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.rfid_scanner.databinding.FragmentTemplateBinding
import com.example.rfid_scanner.module.main.scan.transaction.checkout.qr_reader.QRReaderViewModel
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class TemplateFragment : BaseFragment<FragmentTemplateBinding, TemplateViewModel>() {

    /** Binding fragment with view and viewmodel */
    override fun getViewModelClass() = TemplateViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTemplateBinding.inflate(inflater, container, false)

    override fun retrieveArgs() {}
    override fun setUpViews() = with(binding) {}
    override fun observeData() = with(viewModel) {}
    override fun initEvent() {}

}
package com.example.rfid_scanner.module.template

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.rfid_scanner.databinding.FragmentTemplateBinding
import com.example.rfid_scanner.utils.generic.BaseFragment

class TemplateFragment : BaseFragment<FragmentTemplateBinding, TemplateViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTemplateBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = TemplateViewModel::class.java

    override fun setUpViews() = with(binding) {
        tvTest.text = this.toString().split("{")[0]
    }

    override fun observeData() = with(viewModel) {
        text.observe(viewLifecycleOwner, { binding.tvTest.text = it })
    }



}
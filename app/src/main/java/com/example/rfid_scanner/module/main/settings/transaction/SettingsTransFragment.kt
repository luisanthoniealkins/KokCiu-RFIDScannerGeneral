package com.example.rfid_scanner.module.main.settings.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rfid_scanner.databinding.FragmentSettingsTransBinding
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class SettingsTransFragment : BaseFragment<FragmentSettingsTransBinding, SettingsTransViewModel>(){

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSettingsTransBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = SettingsTransViewModel::class.java

    override fun setUpViews() = with(binding) {
        rvItem.layoutManager = GridLayoutManager(context, 2)
        rvItem.adapter = viewModel.adapter

        imvBack.setOnClickListener { navigateBack() }
        btnConfirm.setOnClickListener {
            viewModel.confirm()
            navigateBack()
        }
    }

}
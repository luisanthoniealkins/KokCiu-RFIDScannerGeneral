package com.example.rfid_scanner.module.main.settings.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rfid_scanner.R
import com.example.rfid_scanner.databinding.FragmentTransSettingsBinding
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class TransSettingsFragment : BaseFragment<FragmentTransSettingsBinding, TransSettingsViewModel>(){

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTransSettingsBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = TransSettingsViewModel::class.java

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
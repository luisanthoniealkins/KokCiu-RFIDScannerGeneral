package com.example.rfid_scanner.module.main.settings.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rfid_scanner.databinding.FragmentTransSettingsBinding

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
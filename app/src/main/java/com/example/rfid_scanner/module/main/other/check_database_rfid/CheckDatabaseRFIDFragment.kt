package com.example.rfid_scanner.module.main.other.check_database_rfid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.databinding.FragmentCheckDatabaseRfidBinding
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment
import com.example.rfid_scanner.utils.listener.ItemClickListener

class CheckDatabaseRFIDFragment : BaseFragment<FragmentCheckDatabaseRfidBinding, CheckDatabaseRFIDViewModel>(),
    ItemClickListener {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCheckDatabaseRfidBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = CheckDatabaseRFIDViewModel::class.java

    override fun setUpViews() = with(binding) {
        btnVerify.setOnClickListener { viewModel.checkDatabaseRFID() }

        rvItem.layoutManager = LinearLayoutManager(requireContext())
        rvItem.adapter = viewModel.adapter
    }

    override fun observeData() = with(viewModel) {
        adapter.listener = this@CheckDatabaseRFIDFragment

        queryState.observeWithOwner {
            binding.btnVerify.isEnabled = it != MResponse.LOADING
        }
    }

    override fun onItemClick(item: Any) {
        val (stockCode, stockName, stockUnitCount) = item as Triple<String, String, Int>
        navigateTo(CheckDatabaseRFIDFragmentDirections.toAdjustmentDatabaseRFIDFragment(stockCode, stockName, stockUnitCount))
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkDatabaseRFID()
    }

}
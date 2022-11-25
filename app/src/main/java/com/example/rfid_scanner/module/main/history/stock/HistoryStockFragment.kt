package com.example.rfid_scanner.module.main.history.stock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.rfid_scanner.R
import com.example.rfid_scanner.databinding.FragmentHistoryStockBinding
import com.example.rfid_scanner.databinding.FragmentHistoryTransactionBinding
import com.example.rfid_scanner.module.main.data.explore.stock.ExploreStockViewModel
import com.example.rfid_scanner.module.main.history.stock.fragment.detail.HistoryStockDetailFragment
import com.example.rfid_scanner.module.main.history.stock.fragment.transaction.HistoryStockTransactionFragment
import com.example.rfid_scanner.module.main.scan.transaction.adjustment.TransAdjustmentFragmentDirections
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment
import com.example.rfid_scanner.utils.helper.DateHelper
import com.google.android.material.tabs.TabLayoutMediator

class HistoryStockFragment : BaseFragment<FragmentHistoryStockBinding, HistoryStockViewModel>() {

    /** Binding fragment with view and viewmodel */
    override fun getViewModelClass() = HistoryStockViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentHistoryStockBinding.inflate(inflater, container, false)

    override fun retrieveArgs() {
        getNavController()?.currentBackStackEntry?.savedStateHandle?.getLiveData<List<String>?>(
            ExploreStockViewModel.KEY_STOCK)?.observeWithOwner {
            if (it == null) {
                navigateBack()
            } else {
                binding.tvStockName.text = it[1]
                viewModel.setStockCode(it[0])
            }
        }
    }

    override fun observeData() = with(viewModel) {
        lvStockCode.observeWithOwner {
            binding.vpContainer.adapter = ViewPagerAdapter(requireActivity())
            TabLayoutMediator(binding.tabLayout, binding.vpContainer) { tab, position ->
                tab.text = HistoryStockViewModel.tabHeaders[position]
            }.attach()
        }
    }

    override fun initEvent() {
        if (viewModel.isInitialEntry) {
            viewModel.isInitialEntry = false
            navigateTo(HistoryStockFragmentDirections.toExploreStockFragment3(true))
        }
    }

    inner class ViewPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
        override fun getItemCount() = HistoryStockViewModel.tabHeaders.size

        override fun createFragment(position: Int) =
            when(position) {
                0 -> HistoryStockDetailFragment(viewModel.currentStockCode)
                else -> HistoryStockTransactionFragment(viewModel.currentStockCode, binding.tabLayout.getTabAt(1))
            }
    }
}
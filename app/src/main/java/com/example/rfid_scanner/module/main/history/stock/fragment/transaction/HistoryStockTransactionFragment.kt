package com.example.rfid_scanner.module.main.history.stock.fragment.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.R
import com.example.rfid_scanner.databinding.FragmentHistoryStockTransactionBinding
import com.example.rfid_scanner.databinding.FragmentTemplateBinding
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment
import com.example.rfid_scanner.utils.helper.DateHelper
import com.google.android.material.tabs.TabLayout.Tab

class HistoryStockTransactionFragment(
    val stockCode: String,
    val tab: Tab?,
) : BaseFragment<FragmentHistoryStockTransactionBinding, HistoryStockTransactionViewModel>() {

    /** Binding fragment with view and viewmodel */
    override fun getViewModelClass() = HistoryStockTransactionViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentHistoryStockTransactionBinding.inflate(inflater, container, false)

    override fun retrieveArgs() {
        viewModel.setStockCode(stockCode)
    }

    override fun setUpViews() = with(binding) {
        val dates = mutableListOf<String>()
        dates.add(DateHelper.getFormattedDateTime("MMMM, yyyy", DateHelper.currentDate))
        actvDate.setAdapter(ArrayAdapter(requireContext(), R.layout.item_date, dates))
        actvDate.setText(dates[0], false)
        actvDate.setOnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
            viewModel.getStockTransaction(DateHelper.getDate("MMMM, yyyy", adapterView.getItemAtPosition(i).toString())!!)
        }

        listOf(chipCheckIn, chipCheckOut, chipReturn, chipBroken, chipClear, chipAdjustment).map {
            it.setOnCheckedChangeListener { _, _ -> applyFilter()}
        }

        viewModel.adapter.setTab(tab)
        rvItem.layoutManager = LinearLayoutManager(requireContext())
        rvItem.adapter = viewModel.adapter
    }

    override fun observeData() = with(viewModel) {
        lvTransactionDates.observeWithOwner {
            binding.actvDate.setAdapter(ArrayAdapter(requireContext(), R.layout.item_date, it))
        }
    }

    override fun initEvent() {
        applyFilter()
        viewModel.getAllTransactionDates()
        viewModel.getStockTransaction(DateHelper.currentDate)
    }

    private fun applyFilter() = with(binding) {
        viewModel.adapter.setChecked(
            chipCheckIn.isChecked,
            chipCheckOut.isChecked,
            chipReturn.isChecked,
            chipBroken.isChecked,
            chipClear.isChecked,
            chipAdjustment.isChecked,
        )
    }

}
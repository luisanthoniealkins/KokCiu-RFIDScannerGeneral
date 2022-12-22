package com.example.rfid_scanner.module.main.history.stock.fragment.transaction

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.R
import com.example.rfid_scanner.databinding.FragmentHistoryStockTransactionBinding
import com.example.rfid_scanner.module.main.history.stock.HistoryStockFragmentDirections
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

    @SuppressLint("UseCompatTextViewDrawableApis")
    override fun setUpViews() = with(binding) {
        val dates = mutableListOf<String>()
        dates.add(DateHelper.getFormattedDateTime("MMMM, yyyy", DateHelper.currentDate))
        actvDate.setAdapter(ArrayAdapter(requireContext(), R.layout.item_date, dates))
        actvDate.setText(dates[0], false)
        actvDate.setOnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
            viewModel.getStockTransaction(DateHelper.getDate("MMMM, yyyy", adapterView.getItemAtPosition(i).toString())!!)
        }

        listOf(
            chipCheckIn, chipCheckOut, chipReturn, chipBroken, chipClear, chipAdjustment, chipOthers
        ).map {
            it.setOnCheckedChangeListener { _, _ -> applyFilter() }
        }

        viewModel.adapter.setTab(tab)
        rvItem.layoutManager = LinearLayoutManager(requireContext())
        rvItem.adapter = viewModel.adapter

        btnFilter.setOnClickListener {
            viewModel.showFilterList = !viewModel.showFilterList
            llChipContainer1.visibility = if (viewModel.showFilterList) View.VISIBLE else View.GONE
            llChipContainer2.visibility = if (viewModel.showFilterList) View.VISIBLE else View.GONE
            llChipContainer3.visibility = if (viewModel.showFilterList) View.VISIBLE else View.GONE
            dvFilter.visibility = if (viewModel.showFilterList) View.VISIBLE else View.GONE

            btnFilter.setTextColor(ContextCompat.getColor(requireContext(),
                if (viewModel.showFilterList) R.color.purple_500
                else R.color.dark_gray_default_boundary_color
            ))
            btnFilter.compoundDrawableTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (viewModel.showFilterList) R.color.purple_500
                    else R.color.dark_gray_default_boundary_color
                )
            )
            btnFilter.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (viewModel.showFilterList) R.drawable.ic_baseline_arrow_drop_up_24
                else R.drawable.ic_baseline_arrow_drop_down_24,
                0
            )
        }
    }

    override fun observeData() = with(viewModel) {
        lvTransactionDates.observeWithOwner {
            binding.actvDate.setAdapter(ArrayAdapter(requireContext(), R.layout.item_date, it.reversed()))
        }
    }

    override fun initEvent() {
        applyFilter()
        viewModel.getAllTransactionDates()
        viewModel.getStockTransaction(DateHelper.currentDate)

//        navigateTo(
//            HistoryStockFragmentDirections.toTransactionRFIDFragment("JL1234512")
//        )
    }

    private fun applyFilter() = with(binding) {
        viewModel.adapter.setChecked(
            chipCheckIn.isChecked,
            chipCheckOut.isChecked,
            chipReturn.isChecked,
            chipBroken.isChecked,
            chipClear.isChecked,
            chipAdjustment.isChecked,
            chipOthers.isChecked
        )
    }
}
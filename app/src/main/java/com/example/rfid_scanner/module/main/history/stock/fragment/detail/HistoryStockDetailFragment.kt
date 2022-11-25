package com.example.rfid_scanner.module.main.history.stock.fragment.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.rfid_scanner.R
import com.example.rfid_scanner.databinding.FragmentHistoryStockDetailBinding
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment
import com.example.rfid_scanner.utils.helper.DateHelper

class HistoryStockDetailFragment(
    val stockCode: String
) : BaseFragment<FragmentHistoryStockDetailBinding, HistoryStockDetailViewModel>() {

    /** Binding fragment with view and viewmodel */
    override fun getViewModelClass() = HistoryStockDetailViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentHistoryStockDetailBinding.inflate(inflater, container, false)

    override fun retrieveArgs() {
        viewModel.setStockCode(stockCode)
    }

    override fun setUpViews() = with(binding) {
        val dates = mutableListOf<String>()
        dates.add(DateHelper.getFormattedDateTime("MMMM, yyyy", DateHelper.currentDate))
        actvDate.setAdapter(ArrayAdapter(requireContext(), R.layout.item_date, dates))
        actvDate.setText(dates[0], false)
        actvDate.setOnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
            viewModel.getStockDetail(DateHelper.getDate("MMMM, yyyy", adapterView.getItemAtPosition(i).toString())!!)
        }
    }

    override fun observeData() = with(viewModel) {
        lvTransactionDates.observeWithOwner {
            binding.actvDate.setAdapter(ArrayAdapter(requireContext(), R.layout.item_date, it))
        }

        lvDetailStock.observeWithOwner {
            binding.tvStockCode.text = it.code
            binding.tvStockBrand.text = it.brand
            binding.tvStockVehicleType.text = it.vehicleType
            binding.tvStockUnit.text = it.unit

            binding.tvCheckIn.text = it.checkInStock.toString()
            binding.tvCheckOut.text = it.checkOutStock.toString()
            binding.tvReturn.text = it.returnStock.toString()
            binding.tvBroken.text = it.brokenStock.toString()
            binding.tvClear.text = it.clearStock.toString()
            binding.tvLost.text = it.lostStock.toString()
            binding.tvAvailableStock.text = it.availableStock.toString()
            binding.tvCurrentStock.text = it.curStock.toString()
            binding.tvPreviousStock.text = it.prevStock.toString()
        }
    }

    override fun initEvent() {
        viewModel.getAllTransactionDates()
        viewModel.getStockDetail(DateHelper.currentDate)
    }

}
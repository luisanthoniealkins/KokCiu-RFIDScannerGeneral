package com.example.rfid_scanner.module.main.history.transaction

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Bill
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.databinding.FragmentHistoryTransactionBinding
import com.example.rfid_scanner.module.main.menu.MenuFragmentDirections
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment
import com.example.rfid_scanner.utils.helper.DateHelper

class HistoryTransactionFragment : BaseFragment<FragmentHistoryTransactionBinding, HistoryTransactionViewModel>() {

    /** Binding fragment with view and viewmodel */
    override fun getViewModelClass() = HistoryTransactionViewModel::class.java
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentHistoryTransactionBinding.inflate(inflater, container, false)


    @SuppressLint("UseCompatTextViewDrawableApis")
    override fun setUpViews() = with(binding) {
        toolbar.inflateMenu(R.menu.menu_search)
        toolbar.title = "Daftar Transaksi"
        val searchItem = toolbar.menu.findItem(R.id.menu_action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String) = false
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.adapter.filter.filter(newText)
                return false
            }
        })

        val dates = mutableListOf<String>()
        dates.add(DateHelper.getFormattedDateTime("MMMM, yyyy", DateHelper.currentDate))
        actvDate.setAdapter(ArrayAdapter(requireContext(), R.layout.item_date, dates))
        actvDate.setText(dates[0], false)
        actvDate.setOnItemClickListener { adapterView: AdapterView<*>, _: View?, i: Int, _: Long ->
            viewModel.setTransactionDate(adapterView.getItemAtPosition(i).toString())
        }

        chipCheckIn.isChecked = viewModel.checkInChecked
        chipCheckOut.isChecked = viewModel.checkOutChecked
        chipReturn.isChecked = viewModel.returnChecked
        chipBroken.isChecked = viewModel.brokenChecked
        chipClear.isChecked = viewModel.clearChecked
        chipAdjustment.isChecked = viewModel.adjustChecked
        chipOthers.isChecked = viewModel.othersChecked
        listOf(
            chipCheckIn, chipCheckOut, chipReturn, chipBroken, chipClear, chipAdjustment, chipOthers
        ).map {
            it.setOnCheckedChangeListener { _, _ -> applyFilter()}
        }

        rvItem.layoutManager = LinearLayoutManager(requireContext())
        rvItem.adapter = viewModel.adapter

        btnFilter.text = "Filter (${viewModel.getFilterCount()})"
        btnFilter.setOnClickListener {
            viewModel.showFilterList = !viewModel.showFilterList
            llChipContainer1.visibility = if (viewModel.showFilterList) View.VISIBLE else View.GONE
            llChipContainer2.visibility = if (viewModel.showFilterList) View.VISIBLE else View.GONE
            llChipContainer3.visibility = if (viewModel.showFilterList) View.VISIBLE else View.GONE
            dvFilter.visibility = if (viewModel.showFilterList) View.VISIBLE else View.GONE

            btnFilter.setTextColor(
                ContextCompat.getColor(requireContext(),
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

        btnQueryAll.setOnClickListener {
            viewModel.getAllTransactions(isLimited = false)
        }
    }


    private fun applyFilter() = with(binding) {
        viewModel.setFilter(
            chipCheckIn.isChecked,
            chipCheckOut.isChecked,
            chipReturn.isChecked,
            chipBroken.isChecked,
            chipClear.isChecked,
            chipAdjustment.isChecked,
            chipOthers.isChecked
        )
        btnFilter.text = "Filter (${viewModel.getFilterCount()})"
    }

    override fun observeData() = with(viewModel) {
        lvTransactionDates.observeWithOwner {
            binding.actvDate.setAdapter(ArrayAdapter(requireContext(), R.layout.item_date, it.reversed()))
            viewModel.setTransactionDate(binding.actvDate.text.toString())
        }

        lvSelectedItem.observeWithOwner { heCode ->
            heCode.getContentIfNotHandled()?.let {
                if (it is Bill) {
                    navigateBack()
                    navigateTo(MenuFragmentDirections.toPrintCheckoutFragment(arrayOf(it)))
                } else {
                    navigateTo(HistoryTransactionFragmentDirections.toTransactionRFIDFragment(it as String))
                }
            }
        }

        lvQueryState.observeWithOwner {
            binding.pbLoading.visibility = if (it.state == MResponse.LOADING) View.VISIBLE else View.GONE
            binding.imvDone.visibility = if (it.state == MResponse.FINISHED_SUCCESS) View.VISIBLE else View.GONE
            binding.imvFail.visibility = if (it.state == MResponse.FINISHED_FAILURE) View.VISIBLE else View.GONE
        }

        lvShowQueryAllButton.observeWithOwner {
            binding.btnQueryAll.visibility = if (it) View.VISIBLE else View.GONE
        }

        lvQueryInfo.observeWithOwner {
            binding.tvText.text = it
        }
    }

    override fun initEvent() {
        applyFilter()
        viewModel.getAllTransactionDates()
    }
}
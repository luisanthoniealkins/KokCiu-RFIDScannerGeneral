package com.example.rfid_scanner.module.main.data.info.transaction_rfid

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.R
import com.example.rfid_scanner.databinding.FragmentExploreBinding
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class TransactionRFIDFragment : BaseFragment<FragmentExploreBinding, TransactionRFIDViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentExploreBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = TransactionRFIDViewModel::class.java

    override fun retrieveArgs() {
        val args: TransactionRFIDFragmentArgs by navArgs()
        viewModel.setTransaction(args.transactionCode)
    }

    override fun setUpViews() = with(binding) {
        toolbar.inflateMenu(R.menu.menu_search)
        toolbar.title = "Daftar RFID"
        val searchItem = toolbar.menu.findItem(R.id.menu_action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String) = false
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.rfidAdapter.filter(newText)
                return false
            }
        })

        fabAdd.isVisible = false

        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.rfidAdapter
    }
}
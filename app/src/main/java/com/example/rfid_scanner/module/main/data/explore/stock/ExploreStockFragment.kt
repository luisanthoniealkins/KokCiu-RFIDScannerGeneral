package com.example.rfid_scanner.module.main.data.explore.stock

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.R
import com.example.rfid_scanner.databinding.FragmentExploreBinding
import com.example.rfid_scanner.module.main.data.explore.stock.ExploreStockViewModel.Companion.KEY_STOCK
import com.example.rfid_scanner.module.main.scan.transaction.checkout.qr_reader.QRReaderViewModel
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class ExploreStockFragment : BaseFragment<FragmentExploreBinding, ExploreStockViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentExploreBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = ExploreStockViewModel::class.java

    override fun retrieveArgs() {
        val args: ExploreStockFragmentArgs by navArgs()

        viewModel.searching = args.isSearching
        binding.fabAdd.isVisible = !args.isSearching
    }

    override fun setUpViews(): Unit = with(binding) {
        toolbar.inflateMenu(R.menu.menu_search)
        toolbar.title = "Pilih Barang"
        val searchItem = toolbar.menu.findItem(R.id.menu_action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String) = false
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.exploreAdapter.filter(newText)
                return false
            }
        })

        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.exploreAdapter

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getNavController()?.previousBackStackEntry?.savedStateHandle?.set(
                    KEY_STOCK, null)
                navigateBack()
            }
        })
    }

    override fun observeData() = with(viewModel) {
        selectedItem.observeWithOwner {
            if (searching) {
                getNavController()?.previousBackStackEntry?.savedStateHandle?.set(KEY_STOCK, listOf(it.code, it.name))
                navigateBack()
            } else {

            }
        }
    }

}
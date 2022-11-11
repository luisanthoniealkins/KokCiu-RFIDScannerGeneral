package com.example.rfid_scanner.module.main.explore.stockId

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.R
import com.example.rfid_scanner.databinding.FragmentExploreBinding
import com.example.rfid_scanner.module.main.explore.stockId.ExploreStockIdViewModel.Companion.KEY_STOCK_ID

class ExploreStockIdFragment : BaseFragment<FragmentExploreBinding, ExploreStockIdViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentExploreBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = ExploreStockIdViewModel::class.java

    override fun retrieveArgs() {
        val args: ExploreStockIdFragmentArgs by navArgs()

        viewModel.searching = args.isSearching
        binding.fabAdd.isVisible = !args.isSearching
    }

    override fun setUpViews() = with(binding) {
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
    }

    override fun observeData() = with(viewModel) {
        selectedItem.observeWithOwner {
            if (searching) {
                getNavController()?.previousBackStackEntry?.savedStateHandle?.set(KEY_STOCK_ID, listOf(it.id, it.stock.name))
                navigateBack()
            } else {

            }
        }
    }

}
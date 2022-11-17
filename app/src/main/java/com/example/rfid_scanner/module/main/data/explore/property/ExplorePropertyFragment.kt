package com.example.rfid_scanner.module.main.data.explore.property

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfid_scanner.R
import com.example.rfid_scanner.databinding.FragmentExploreBinding
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.KEY_PROPERTY_BRAND
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.KEY_PROPERTY_CUSTOMER
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.KEY_PROPERTY_UNIT
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.KEY_PROPERTY_VEHICLE_TYPE
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.TYPE_BRAND
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.TYPE_CUSTOMER
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.TYPE_VEHICLE_TYPE
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class ExplorePropertyFragment : BaseFragment<FragmentExploreBinding, ExplorePropertyViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentExploreBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = ExplorePropertyViewModel::class.java

    override fun retrieveArgs() {
        val args: ExplorePropertyFragmentArgs by navArgs()
        viewModel.setMode(args.isSearching, args.type)
    }

    override fun setUpViews() = with(binding) {
        toolbar.inflateMenu(R.menu.menu_search)
        toolbar.title = when(viewModel.type) {
            TYPE_CUSTOMER -> "Pilih Customer"
            TYPE_BRAND -> "Pilih Merek"
            TYPE_VEHICLE_TYPE -> "Pilih Tipe Kendaraan"
            else -> "Pilih Unit"
        }

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

        fabAdd.isVisible = !viewModel.searching
        fabAdd.setOnClickListener {
            navigateTo(
                ExplorePropertyFragmentDirections.toAlterPropertyFragment(
                    viewModel.type,
                    null
                )
            )
        }

        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = viewModel.exploreAdapter
    }

    override fun observeData() = with(viewModel) {
        selectedItem.observeWithOwner {
            if (it.hasBeenHandled) return@observeWithOwner
            if (searching) {
                getNavController()?.previousBackStackEntry?.savedStateHandle?.set(
                    when (viewModel.type) {
                        TYPE_CUSTOMER -> KEY_PROPERTY_CUSTOMER
                        TYPE_BRAND -> KEY_PROPERTY_BRAND
                        TYPE_VEHICLE_TYPE -> KEY_PROPERTY_VEHICLE_TYPE
                        else -> KEY_PROPERTY_UNIT
                    },
                    it.getContentIfNotHandled()
                )
                navigateBack()
            } else {
                navigateTo(
                    ExplorePropertyFragmentDirections.toAlterPropertyFragment(
                        viewModel.type,
                        it.getContentIfNotHandled()
                    )
                )
            }
        }
    }




}
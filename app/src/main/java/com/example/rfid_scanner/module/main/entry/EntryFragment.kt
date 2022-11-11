package com.example.rfid_scanner.module.main.entry

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.rfid_scanner.databinding.FragmentEntryBinding
import com.example.rfid_scanner.module.main.MainViewModel
import com.example.rfid_scanner.module.main.explore.property.ExplorePropertyViewModel.Companion.TYPE_BRAND
import com.example.rfid_scanner.module.main.explore.property.ExplorePropertyViewModel.Companion.TYPE_CUSTOMER
import com.example.rfid_scanner.module.main.explore.property.ExplorePropertyViewModel.Companion.TYPE_UNIT
import com.example.rfid_scanner.module.main.explore.property.ExplorePropertyViewModel.Companion.TYPE_VEHICLE_TYPE
import com.example.rfid_scanner.module.main.transaction.general.TransGeneralViewModel.Companion.BROKEN
import com.example.rfid_scanner.module.main.transaction.general.TransGeneralViewModel.Companion.CHECK_IN
import com.example.rfid_scanner.module.main.transaction.general.TransGeneralViewModel.Companion.CLEAR
import com.example.rfid_scanner.module.main.transaction.general.TransGeneralViewModel.Companion.GENERAL
import com.example.rfid_scanner.module.main.transaction.general.TransGeneralViewModel.Companion.RETURN
import com.example.rfid_scanner.module.main.transaction.general.TransGeneralViewModel.Companion.REUSE
import com.example.rfid_scanner.utils.constant.Constant.SERVICE_STATUS_OK
import com.example.rfid_scanner.utils.custom.view.PasswordDialog

class EntryFragment : BaseFragment<FragmentEntryBinding, EntryViewModel>(),
    PasswordDialog.PasswordDialogListener {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEntryBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = EntryViewModel::class.java

    private val acViewModel: MainViewModel by activityViewModels()

    override fun setUpViews() = with(binding) {
        btnAdminMode.setOnClickListener {
            btnAdminMode.isEnabled = false
            val passwordDialog = PasswordDialog(this@EntryFragment)
            passwordDialog.show(childFragmentManager, "password dialog")
        }

        btnScanTag.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToTagScannerFragment())
        }

        btnTransactionGeneral.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToTransGeneralFragment(GENERAL))
        }

        btnCheckIn.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToTransGeneralFragment(CHECK_IN))
        }

        btnReturn.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToTransGeneralFragment(RETURN))
        }

        btnBroken.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToTransGeneralFragment(BROKEN))
        }

        btnClearTag.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToTransGeneralFragment(CLEAR))
        }

        btnReuseTag.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToTransGeneralFragment(REUSE))
        }

        btnCheckStockroom.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToCheckRoomFragment())
        }

        btnAddEditStockId.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToExploreStockIdFragment(false))
        }

        btnAddEditCustomer.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToExplorePropertyFragment(false, TYPE_CUSTOMER))
        }

        btnAddEditBrand.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToExplorePropertyFragment(false, TYPE_BRAND))
        }

        btnAddEditUnit.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToExplorePropertyFragment(false, TYPE_UNIT))
        }

        btnAddEditVehicleType.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToExplorePropertyFragment(false, TYPE_VEHICLE_TYPE))
        }

        btnSettingsNetwork.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToNetworkSettingFragment())
        }

        btnSettingsTrans.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToTransSettingFragment())
        }
    }

    override fun observeData() = with(viewModel) {
        isAdminUnlocked.observeWithOwner {
            binding.btnAdminMode.visibility = if (it) View.GONE else View.VISIBLE
            binding.llAdmin.visibility = if (it) View.VISIBLE else View.GONE
            binding.llAdminAndOnline.visibility =
                if (it && acViewModel.ldServerStatus.value?.status == SERVICE_STATUS_OK) View.VISIBLE
                else View.GONE
        }

        acViewModel.ldServerStatus.observeWithOwner {
            binding.llOnline.visibility =
                if (it.status == SERVICE_STATUS_OK) View.VISIBLE
                else View.GONE

            binding.llAdminAndOnline.visibility =
                if (isAdminUnlocked.value == true && it.status == SERVICE_STATUS_OK) View.VISIBLE
                else View.GONE
        }
    }

    override fun submitPassword(password: String?) {
        if (!viewModel.submitPassword(password)) showToast("Password admin salah")
    }

    override fun reEnableButton() {
        binding.btnAdminMode.isEnabled = true
    }
}



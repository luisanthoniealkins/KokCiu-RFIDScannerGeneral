package com.example.rfid_scanner.module.main.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.rfid_scanner.databinding.FragmentMenuBinding
import com.example.rfid_scanner.module.main.MainViewModel
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.TYPE_BRAND
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.TYPE_CUSTOMER
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.TYPE_UNIT
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.TYPE_VEHICLE_TYPE
import com.example.rfid_scanner.module.main.scan.transaction.general.TransGeneralViewModel.Companion.BROKEN
import com.example.rfid_scanner.module.main.scan.transaction.general.TransGeneralViewModel.Companion.CHECK_IN
import com.example.rfid_scanner.module.main.scan.transaction.general.TransGeneralViewModel.Companion.CLEAR
import com.example.rfid_scanner.module.main.scan.transaction.general.TransGeneralViewModel.Companion.GENERAL
import com.example.rfid_scanner.module.main.scan.transaction.general.TransGeneralViewModel.Companion.RETURN
import com.example.rfid_scanner.module.main.scan.transaction.general.TransGeneralViewModel.Companion.REUSE
import com.example.rfid_scanner.utils.constant.Constant.SERVICE_STATUS_OK
import com.example.rfid_scanner.utils.custom.view.PasswordDialog
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class MenuFragment : BaseFragment<FragmentMenuBinding, MenuViewModel>(),
    PasswordDialog.PasswordDialogListener {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMenuBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = MenuViewModel::class.java

    private val acViewModel: MainViewModel by activityViewModels()

    override fun setUpViews() = with(binding) {
        btnAdminMode.setOnClickListener {
            btnAdminMode.isEnabled = false
            val passwordDialog = PasswordDialog(this@MenuFragment)
            passwordDialog.show(childFragmentManager, "password dialog")
        }

        btnScanTag.setOnClickListener {
            navigateTo(MenuFragmentDirections.toTagScannerFragment())
        }

        btnTransactionGeneral.setOnClickListener {
            navigateTo(MenuFragmentDirections.toTransGeneralFragment(GENERAL))
        }

        btnCheckIn.setOnClickListener {
            navigateTo(MenuFragmentDirections.toTransGeneralFragment(CHECK_IN))
        }

        btnCheckOut.setOnClickListener {
            navigateTo(MenuFragmentDirections.toTransCheckoutFragment())
        }

        btnReturn.setOnClickListener {
            navigateTo(MenuFragmentDirections.toTransGeneralFragment(RETURN))
        }

        btnBroken.setOnClickListener {
            navigateTo(MenuFragmentDirections.toTransGeneralFragment(BROKEN))
        }

        btnClearTag.setOnClickListener {
            navigateTo(MenuFragmentDirections.toTransGeneralFragment(CLEAR))
        }

        btnReuseTag.setOnClickListener {
            navigateTo(MenuFragmentDirections.toTransGeneralFragment(REUSE))
        }

        btnAdjustment.setOnClickListener {
            navigateTo(MenuFragmentDirections.toTransAdjustmentFragment())
        }

        btnCheckStockroom.setOnClickListener {
            navigateTo(MenuFragmentDirections.toCheckRoomFragment())
        }

        btnHistoryTransaction.setOnClickListener {
            navigateTo(MenuFragmentDirections.toHistoryTransactionFragment())
        }

        btnHistoryStock.setOnClickListener {
            navigateTo(MenuFragmentDirections.toHistoryStockFragment())
        }

        btnAddEditStock.setOnClickListener {
            navigateTo(MenuFragmentDirections.toExploreStockFragment2(false))
        }

        btnAddEditStockId.setOnClickListener {
            navigateTo(MenuFragmentDirections.toExploreStockIdFragment(false))
        }

        btnAddEditCustomer.setOnClickListener {
            navigateTo(MenuFragmentDirections.toExplorePropertyFragment(false, TYPE_CUSTOMER))
        }

        btnAddEditBrand.setOnClickListener {
            navigateTo(MenuFragmentDirections.toExplorePropertyFragment(false, TYPE_BRAND))
        }

        btnAddEditUnit.setOnClickListener {
            navigateTo(MenuFragmentDirections.toExplorePropertyFragment(false, TYPE_UNIT))
        }

        btnAddEditVehicleType.setOnClickListener {
            navigateTo(MenuFragmentDirections.toExplorePropertyFragment(false, TYPE_VEHICLE_TYPE))
        }

        btnCustomPrint.setOnClickListener {
            navigateTo(MenuFragmentDirections.toPrintCustomFragment())
        }

        btnSettingsNetwork.setOnClickListener {
            navigateTo(MenuFragmentDirections.toNetworkSettingFragment())
        }

        btnSettingsTrans.setOnClickListener {
            navigateTo(MenuFragmentDirections.toTransSettingFragment())
        }

        btnSettingsTag.setOnClickListener {
            navigateTo(MenuFragmentDirections.toSettingsTagFragment())
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



package com.example.rfid_scanner.module.main.entry

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.rfid_scanner.databinding.FragmentEntryBinding
import com.example.rfid_scanner.module.main.MainViewModel
import com.example.rfid_scanner.utils.constant.Constant.SERVICE_STATUS_OK
import com.example.rfid_scanner.utils.custom_view.dialog.PasswordDialog
import com.example.rfid_scanner.utils.generic.BaseFragment

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

        btnSettingsNetwork.setOnClickListener {
            navigateTo(EntryFragmentDirections.actionEntryFragmentToNetworkSettingFragment())
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



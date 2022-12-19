package com.example.rfid_scanner.module.main.data.alter.property

import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.GeneralProperty
import com.example.rfid_scanner.databinding.FragmentAlterPropertyBinding
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_BRAND
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_CUSTOMER
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_VEHICLE_TYPE
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment

class AlterPropertyFragment : BaseFragment<FragmentAlterPropertyBinding, AlterPropertyViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAlterPropertyBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = AlterPropertyViewModel::class.java

    override fun retrieveArgs() {
        val args : AlterPropertyFragmentArgs by navArgs()
        viewModel.setMode(args.type, args.property)
    }

    override fun setUpViews() = with(binding) {
        imvBack.setOnClickListener { navigateBack() }

        val prefix = if (viewModel.isCreate) "Tambah" else "Edit"
        tvTitle.text = when(viewModel.type) {
            PROPERTY_TYPE_CUSTOMER -> "$prefix Customer"
            PROPERTY_TYPE_BRAND -> "$prefix Merek"
            PROPERTY_TYPE_VEHICLE_TYPE -> "$prefix Tipe Kendaraan"
            else -> "$prefix Unit"
        }

        edtCode.filters = arrayOf<InputFilter>(AllCaps())
        edtName.filters = arrayOf<InputFilter>(AllCaps())
        if (!viewModel.isCreate) {
            edtCode.isEnabled = false
            edtCode.setTextColor(gColor(R.color.dark_gray_item_text_disable))
            edtCode.setText(viewModel.property?.propertyCode)
            edtName.setText(viewModel.property?.propertyName)
        }

        btnConfirm.text = prefix
        btnConfirm.setOnClickListener {
            validateInput()?.let {
                binding.btnConfirm.isEnabled = false
                viewModel.saveData(it)
            }
        }
    }

    private fun validateInput(): GeneralProperty? {
        val code: String = binding.edtCode.text.toString().trim { it <= ' ' }
        val name: String = binding.edtName.text.toString().trim { it <= ' ' }

        binding.tilCode.error = if (code.isEmpty()) "Kode harus diisi" else ""
        binding.tilName.error = if (name.isEmpty()) "Nama harus diisi" else ""

        val isError = code.isEmpty() || name.isEmpty()
        return if (isError) null else GeneralProperty(code, name)
    }

    override fun observeData() = with(viewModel) {
        saveComplete.observeWithOwner {
            if (it) navigateBack()
            else binding.btnConfirm.isEnabled = true
        }
    }




}
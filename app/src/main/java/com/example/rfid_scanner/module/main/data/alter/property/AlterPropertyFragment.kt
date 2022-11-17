package com.example.rfid_scanner.module.main.data.alter.property

import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.rfid_scanner.databinding.FragmentAlterPropertyBinding
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.TYPE_BRAND
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.TYPE_CUSTOMER
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.TYPE_VEHICLE_TYPE
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment
import java.util.*

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
            TYPE_CUSTOMER -> "$prefix Customer"
            TYPE_BRAND -> "$prefix Merek"
            TYPE_VEHICLE_TYPE -> "$prefix Tipe Kendaraan"
            else -> "$prefix Unit"
        }

        edtCode.filters = arrayOf<InputFilter>(AllCaps())
        edtName.filters = arrayOf<InputFilter>(AllCaps())
        if (!viewModel.isCreate) {
            edtCode.setText(viewModel.property?.propertyCode)
            edtName.setText(viewModel.property?.propertyName)
        }

        btnConfirm.text = prefix
        btnConfirm.setOnClickListener {

            if (viewModel.isCreate) {

            } else {

            }
        }

    }

//    private fun getPropertyFromView(): GeneralProperty? {
//        val code: String = Objects.requireNonNull(mEDTCode.getText()).toString().trim { it <= ' ' }
//        val name: String = Objects.requireNonNull(mEDTName.getText()).toString().trim { it <= ' ' }
//        var isError = false
//        if (code.isEmpty()) {
//            isError = true
//            mTILCode.setError("Kode harus diisi")
//        } else {
//            mTILCode.setError("")
//        }
//        if (name.isEmpty()) {
//            isError = true
//            mTILName.setError("Nama harus diisi")
//        } else {
//            mTILName.setError("")
//        }
//        return if (isError) null else GeneralProperty(code, name)
//    }

    override fun observeData() = with(viewModel) {

    }




}
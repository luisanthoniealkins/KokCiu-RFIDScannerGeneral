package com.example.rfid_scanner.module.main.data.alter.stockId

import android.content.DialogInterface
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.navArgs
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Stock
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.data.repository.component.ResponseCode
import com.example.rfid_scanner.databinding.FragmentAlterStockIdBinding
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment
import com.example.rfid_scanner.utils.helper.NumberUtil
import com.example.rfid_scanner.utils.helper.TextHelper.getIdFromCodeAndUnitCount
import java.util.*

class AlterStockIdFragment : BaseFragment<FragmentAlterStockIdBinding, AlterStockIdViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAlterStockIdBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = AlterStockIdViewModel::class.java

    override fun retrieveArgs() {
        val args : AlterStockIdFragmentArgs by navArgs()
        viewModel.stockCode = args.stockCode
    }

    override fun setUpViews() = with(binding) {
        imvBack.setOnClickListener { navigateBack() }

        edtStockId.setText(getIdFromCodeAndUnitCount(viewModel.stockCode,edtUnitCount.text))
        edtUnitCount.doOnTextChanged { text, _, _, _ ->
            edtStockId.setText(getIdFromCodeAndUnitCount(viewModel.stockCode,text))
        }

        btnConfirm.setOnClickListener {
            validateInput()?.let {
                AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin untuk menjalankan operasi?")
                    .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                        btnConfirm.isEnabled = false
                        viewModel.saveData(it)
                    }
                    .setNegativeButton("Batal") { _: DialogInterface?, _: Int -> }
                    .create()
                    .show()
            }
        }

        edtStockId.isEnabled = false
        edtStockId.setTextColor(gColor(R.color.dark_gray_item_text_disable))
    }

    private fun validateInput(): StockId? {
        val stockId = binding.edtStockId.text.toString().trim { it <= ' ' }
        val unitCount = binding.edtUnitCount.text.toString().trim { it <= ' ' }


        if (unitCount.isEmpty()) {
            binding.tilUnitCount.error = "Jumlah unit harus diisi"
            return null
        } else if (!NumberUtil.isNumeric(unitCount)) {
            binding.tilUnitCount.error = "Jumlah unit harus terdiri dari angka 0-9"
            return null
        } else if (!NumberUtil.isBetweenIntRange(unitCount)) {
            binding.tilUnitCount.error = "Jumlah unit maksimal 9 digit angka"
            return null
        }

        val unitCountInt: Int = unitCount.toInt()
        if (unitCountInt <= 0) {
            binding.tilUnitCount.error = "Jumlah unit harus lebih dari 0"
            return null
        } else {
            binding.tilUnitCount.error = ""
        }

        return StockId(Stock(viewModel.stockCode), stockId, unitCountInt )
    }

    override fun observeData() = with(viewModel) {
        saveComplete.observeWithOwner {
            if (it.code == ResponseCode.OK) navigateBack()
            else {
                binding.btnConfirm.isEnabled = true
                showToast(it.message)
            }
        }
    }
}
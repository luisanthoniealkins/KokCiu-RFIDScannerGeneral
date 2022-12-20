package com.example.rfid_scanner.module.main.data.alter.stock

import android.content.DialogInterface
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.navArgs
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.GeneralProperty
import com.example.rfid_scanner.data.model.Stock
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.data.repository.component.ResponseCode
import com.example.rfid_scanner.databinding.FragmentAlterStockBinding
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.KEY_PROPERTY_BRAND
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.KEY_PROPERTY_UNIT
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel.Companion.KEY_PROPERTY_VEHICLE_TYPE
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_BRAND
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_UNIT
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_VEHICLE_TYPE
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment
import com.example.rfid_scanner.utils.helper.NumberUtil
import com.example.rfid_scanner.utils.helper.TextHelper.emptyString
import com.example.rfid_scanner.utils.helper.TextHelper.getIdFromCodeAndUnitCount

class AlterStockFragment : BaseFragment<FragmentAlterStockBinding, AlterStockViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAlterStockBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = AlterStockViewModel::class.java

    override fun retrieveArgs() {
        val args : AlterStockFragmentArgs by navArgs()
        viewModel.setData(
            args.stockCode,
            args.stockName,
            args.stockBrand,
            args.stockVehicleType,
            args.stockUnit
        )

        getNavController()?.currentBackStackEntry?.savedStateHandle?.let {
            it.getLiveData<GeneralProperty>(KEY_PROPERTY_BRAND).observeWithOwner { brand ->
                viewModel.setBrand(brand)
            }
            it.getLiveData<GeneralProperty>(KEY_PROPERTY_VEHICLE_TYPE).observeWithOwner { vehicleType ->
                viewModel.setVehicleType(vehicleType)
            }
            it.getLiveData<GeneralProperty>(KEY_PROPERTY_UNIT).observeWithOwner { unit ->
                viewModel.setUnit(unit)
            }
        }
    }

    override fun setUpViews() = with(binding) {
        imvBack.setOnClickListener { navigateBack() }

        tvTitle.text = if (viewModel.isCreate) "Tambah Barang" else "Edit Barang"

        edtCode.filters = arrayOf<InputFilter>(AllCaps())
        edtName.filters = arrayOf<InputFilter>(AllCaps())
        if (!viewModel.isCreate) {
            edtCode.isEnabled = false
            edtCode.setTextColor(gColor(R.color.dark_gray_item_text_disable))
            edtCode.setText(viewModel.currentStock.code)
            edtName.setText(viewModel.currentStock.name)
            edtUnitCount.visibility = View.GONE
        }

        btnBrand.setOnClickListener {
            navigateTo(AlterStockFragmentDirections.toExplorePropertyFragment(true, PROPERTY_TYPE_BRAND))
        }

        btnVehicleType.setOnClickListener {
            navigateTo(AlterStockFragmentDirections.toExplorePropertyFragment(true, PROPERTY_TYPE_VEHICLE_TYPE))
        }

        btnUnit.setOnClickListener {
            navigateTo(AlterStockFragmentDirections.toExplorePropertyFragment(true, PROPERTY_TYPE_UNIT))
        }

        btnConfirm.text = if (viewModel.isCreate) "Tambah" else "Edit"
        btnConfirm.setOnClickListener {
            validateInput()?.let {
                AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin untuk menjalankan operasi?")
                    .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                        binding.btnConfirm.isEnabled = false
                        viewModel.saveData(it)
                    }
                    .setNegativeButton("Batal") { _: DialogInterface?, _: Int -> }
                    .create()
                    .show()
            }
        }
    }

    private fun validateInput(): StockId? {
        val code: String = binding.edtCode.text.toString().trim { it <= ' ' }
        val name: String = binding.edtName.text.toString().trim { it <= ' ' }
        val unitCount = binding.edtUnitCount.text.toString().trim { it <= ' ' }

        var isError = code.isEmpty() ||
                name.isEmpty() ||
                !viewModel.hasPickedBrand ||
                !viewModel.hasPickedVehicleType ||
                !viewModel.hasPickedUnit

        binding.tilCode.error = if (code.isEmpty()) "Kode harus diisi" else emptyString()
        binding.tilName.error = if (name.isEmpty()) "Nama harus diisi" else emptyString()

        binding.tvBrandError.text = if (!viewModel.hasPickedBrand) "Merek harus dipilih" else emptyString()
        binding.tvBrandError.visibility = if (!viewModel.hasPickedBrand) View.VISIBLE else View.GONE

        binding.tvVehicleTypeError.text = if (!viewModel.hasPickedVehicleType) "Tipe Kendaraan harus dipilih" else emptyString()
        binding.tvVehicleTypeError.visibility = if (!viewModel.hasPickedVehicleType) View.VISIBLE else View.GONE

        binding.tvUnitError.text = if (!viewModel.hasPickedUnit) "Satuan harus dipilih" else emptyString()
        binding.tvUnitError.visibility = if (!viewModel.hasPickedUnit) View.VISIBLE else View.GONE

        if (unitCount.isEmpty()) {
            binding.tilUnitCount.error = "Jumlah unit harus diisi"
            isError = true
        } else if (!NumberUtil.isNumeric(unitCount)) {
            binding.tilUnitCount.error = "Jumlah unit harus terdiri dari angka 0-9"
            isError = true
        } else if (!NumberUtil.isBetweenIntRange(unitCount)) {
            binding.tilUnitCount.error = "Jumlah unit maksimal 9 digit angka"
            isError = true
        }

        val unitCountInt: Int = unitCount.toInt()
        if (unitCountInt <= 0) {
            binding.tilUnitCount.error = "Jumlah unit harus lebih dari 0"
            isError = true
        } else {
            binding.tilUnitCount.error = ""
        }

        return if (isError) null else StockId(
            stock = Stock(
                code = code,
                name = name,
                brand = viewModel.currentStock.brand,
                vehicleType = viewModel.currentStock.vehicleType,
                unit = viewModel.currentStock.unit,
            ),
            unitCount = unitCountInt,
            id = getIdFromCodeAndUnitCount(code,unitCountInt)
        )
    }

    override fun observeData() = with(viewModel) {

        lvBrandName.observeWithOwner { binding.btnBrand.text = it }
        lvVehicleTypeName.observeWithOwner { binding.btnVehicleType.text = it }
        lvUnitName.observeWithOwner { binding.btnUnit.text = it }

        saveComplete.observeWithOwner {
            if (it.code == ResponseCode.OK) navigateBack()
            else {
                binding.btnConfirm.isEnabled = true
                showToast(it.message)
            }
        }
    }
}
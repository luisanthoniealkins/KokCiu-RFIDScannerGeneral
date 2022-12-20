package com.example.rfid_scanner.module.main.data.alter.stock

import android.os.Debug
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.GeneralProperty
import com.example.rfid_scanner.data.model.Stock
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.ADD_STOCK
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.ADD_STOCK_ID
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.EDIT_STOCK
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.data.repository.component.ResponseCode
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel

import com.example.rfid_scanner.utils.helper.TextHelper.emptyString
import kotlinx.coroutines.launch

class AlterStockViewModel : BaseViewModel() {

    var currentStock: Stock = Stock(emptyString())
    var isCreate = false

    var hasPickedBrand = false
    var hasPickedVehicleType = false
    var hasPickedUnit = false

    private val _lvBrandName = MutableLiveData<String>()
    val lvBrandName: LiveData<String> = _lvBrandName

    private val _lvVehicleTypeName = MutableLiveData<String>()
    val lvVehicleTypeName: LiveData<String> = _lvVehicleTypeName

    private val _lvUnitName = MutableLiveData<String>()
    val lvUnitName: LiveData<String> = _lvUnitName

    private val _saveComplete = MutableLiveData<MResponse.ResponseData>()
    val saveComplete: LiveData<MResponse.ResponseData> = _saveComplete

    fun setData(
        stockCode: String?,
        stockName: String?,
        stockBrand: String?,
        stockVehicleType: String?,
        stockUnit: String?
    ) {
        hasPickedBrand = false
        hasPickedVehicleType = false
        hasPickedUnit = false

        stockCode?.let {
            currentStock = Stock(it, stockName)
            getCodesInStock(it)
        }
        isCreate = (stockCode == null)

        stockBrand?.let { _lvBrandName.postValue(it) }
        stockVehicleType?.let { _lvVehicleTypeName.postValue(it) }
        stockUnit?.let { _lvUnitName.postValue(it) }
    }

    private fun getCodesInStock(stockCode: String) {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.GET_STOCK_CODE,
                RequestParam.getStockCode(stockCode),
                RequestResult::getStockCode
            ).collect{ res ->
                res.response?.let {
                    val codesInStock = it.data as Stock
                    Log.d("12345", "1$hasPickedBrand")
                    Log.d("12345", "2$hasPickedVehicleType")
                    Log.d("12345", "3$hasPickedUnit")

                    if (!hasPickedBrand) {
                        currentStock.brand = codesInStock.brand
                        hasPickedBrand = true
                    }

                    if (!hasPickedVehicleType) {
                        currentStock.vehicleType = codesInStock.vehicleType
                        hasPickedVehicleType = true
                    }

                    if (!hasPickedUnit) {
                        currentStock.unit = codesInStock.unit
                        hasPickedUnit = true
                    }
                }
            }
        }
    }

    fun setBrand(brand: GeneralProperty) {
        brand.propertyCode?.let {
            currentStock.brand = it
            hasPickedBrand = true
        }
        brand.propertyName?.let { _lvBrandName.postValue(it) }
    }

    fun setVehicleType(vehicleType: GeneralProperty) {
        vehicleType.propertyCode?.let {
            currentStock.vehicleType = it
            hasPickedVehicleType = true
        }
        vehicleType.propertyName?.let { _lvVehicleTypeName.postValue(it) }
    }

    fun setUnit(unit: GeneralProperty) {
        unit.propertyCode?.let {
            currentStock.unit = it
            hasPickedUnit = true
        }
        unit.propertyName?.let { _lvUnitName.postValue(it) }
    }

    fun saveData(newStockId: StockId) {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                if (isCreate) ADD_STOCK else EDIT_STOCK,
                RequestParam.addEditStock(newStockId.stock),
                RequestResult::getGeneralResponse
            ).collect{ res ->
                res.response?.let {
                    if (isCreate && it.code == ResponseCode.OK) {
                        VolleyRepository.getI().requestAPI(
                            ADD_STOCK_ID,
                            RequestParam.addEditStockId(newStockId),
                            RequestResult::getGeneralResponse
                        ).collect{ res2 ->
                            res2.response?.let { it2 -> _saveComplete.postValue(it2) }
                        }
                    } else {
                        _saveComplete.postValue(it)
                    }
                }
            }
        }
    }
}
package com.example.rfid_scanner.module.main.data.alter.stockId

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.GeneralProperty
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.ADD_BRAND
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.ADD_CUSTOMER
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.ADD_STOCK_ID
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.ADD_UNIT
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.ADD_VEHICLE_TYPE
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.EDIT_BRAND
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.EDIT_CUSTOMER
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.EDIT_UNIT
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.EDIT_VEHICLE_TYPE
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.data.repository.component.ResponseCode
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_BRAND
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_CUSTOMER
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_VEHICLE_TYPE
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class AlterStockIdViewModel : BaseViewModel() {

    var stockCode: String = ""

    private val _saveComplete = MutableLiveData<MResponse.ResponseData>()
    val saveComplete: LiveData<MResponse.ResponseData> = _saveComplete

    fun saveData(it: StockId) {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                ADD_STOCK_ID,
                RequestParam.addEditStockId(it),
                RequestResult::getGeneralResponse
            ).collect{ res ->
                res.response?.let { _saveComplete.postValue(it) }
            }
        }
    }

}
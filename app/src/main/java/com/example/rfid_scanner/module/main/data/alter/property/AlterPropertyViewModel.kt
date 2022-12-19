package com.example.rfid_scanner.module.main.data.alter.property

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.GeneralProperty
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.ADD_BRAND
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.ADD_CUSTOMER
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.ADD_UNIT
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.ADD_VEHICLE_TYPE
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.EDIT_BRAND
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.EDIT_CUSTOMER
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.EDIT_UNIT
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.EDIT_VEHICLE_TYPE
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.data.repository.component.ResponseCode
import com.example.rfid_scanner.module.main.data.explore.property.ExplorePropertyViewModel
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_BRAND
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_CUSTOMER
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_VEHICLE_TYPE
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class AlterPropertyViewModel : BaseViewModel() {

    var type: Int = 0
    var property: GeneralProperty? = null
    var isCreate = false

    private val _saveComplete = MutableLiveData<Boolean>()
    val saveComplete: LiveData<Boolean> = _saveComplete

    fun setMode(type: Int, property: GeneralProperty?) {
        this.type = type
        this.property = property
        isCreate = (property == null)
    }

    fun saveData(generalProperty: GeneralProperty) {
        val endpoint = if (isCreate) {
            when(type) {
                PROPERTY_TYPE_CUSTOMER -> ADD_CUSTOMER
                PROPERTY_TYPE_BRAND -> ADD_BRAND
                PROPERTY_TYPE_VEHICLE_TYPE -> ADD_VEHICLE_TYPE
                else -> ADD_UNIT
            }
        } else {
            when(type) {
                PROPERTY_TYPE_CUSTOMER -> EDIT_CUSTOMER
                PROPERTY_TYPE_BRAND -> EDIT_BRAND
                PROPERTY_TYPE_VEHICLE_TYPE -> EDIT_VEHICLE_TYPE
                else -> EDIT_UNIT
            }
        }

        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                endpoint,
                RequestParam.addEditGeneralProperty(generalProperty),
                RequestResult::getGeneralResponse
            ).collect{ res ->
                _saveComplete.postValue(res.response?.code == ResponseCode.OK)
            }
        }
    }


}
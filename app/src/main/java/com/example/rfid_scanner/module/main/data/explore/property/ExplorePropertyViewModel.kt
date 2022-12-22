package com.example.rfid_scanner.module.main.data.explore.property

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.GeneralProperty
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_BRAND
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_CUSTOMER
import com.example.rfid_scanner.utils.constant.Constant.PROPERTY_TYPE_VEHICLE_TYPE
import com.example.rfid_scanner.utils.custom.kclass.HandledEvent
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import com.example.rfid_scanner.utils.listener.ItemClickListener
import kotlinx.coroutines.launch

class ExplorePropertyViewModel : BaseViewModel(), ItemClickListener {

    companion object {
        const val KEY_PROPERTY_CUSTOMER = "keyPropertysCustomer"
        const val KEY_PROPERTY_BRAND = "keyPropertyBrand"
        const val KEY_PROPERTY_VEHICLE_TYPE = "keyPropertyVehicleType"
        const val KEY_PROPERTY_UNIT = "keyPropertyUnit"
    }

    val exploreAdapter = ExplorePropertyAdapter(this)
    var searching = false
    var type: Int = 0

    private val _selectedItem = MutableLiveData<HandledEvent<GeneralProperty>>()
    val selectedItem : LiveData<HandledEvent<GeneralProperty>> = _selectedItem

    fun setMode(searching: Boolean, type: Int) {
        this.searching = searching
        this.type = type
        getAllProperty(type)
    }

    private fun getAllProperty(type: Int) {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                when(type) {
                    PROPERTY_TYPE_CUSTOMER -> RequestEndPoint.GET_ALL_CUSTOMERS
                    PROPERTY_TYPE_BRAND -> RequestEndPoint.GET_ALL_BRANDS
                    PROPERTY_TYPE_VEHICLE_TYPE -> RequestEndPoint.GET_ALL_VEHICLE_TYPES
                    else -> RequestEndPoint.GET_ALL_UNITS
                },
                null,
                RequestResult::getAllGeneralProperties
            ).collect{ res ->
                res.response?.data?.let { addProperties(it as List<GeneralProperty>) }
            }
        }
    }

    private fun addProperties(list: List<GeneralProperty>) {
        exploreAdapter.setProperty(list)
    }

    override fun onItemClick(item: Any) {
        _selectedItem.postValue(HandledEvent(item as GeneralProperty))
    }




}
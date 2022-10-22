package com.example.rfid_scanner.module.main.explore.property

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.GeneralProperty
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.helper.RequestEndPoint
import com.example.rfid_scanner.data.repository.helper.RequestResult
import com.example.rfid_scanner.utils.generic.BaseViewModel
import com.example.rfid_scanner.utils.generic.HandledEvent
import com.example.rfid_scanner.utils.generic.ItemClickListener
import kotlinx.coroutines.launch

class ExplorePropertyViewModel : BaseViewModel(), ItemClickListener {

    companion object {
        const val TYPE_CUSTOMER = 0
        const val TYPE_BRAND = 1
        const val TYPE_VEHICLE_TYPE = 2
        const val TYPE_UNIT = 3

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

    private suspend fun getAllProperty(type: Int) {
        VolleyRepository.getI().requestAPI(
            when(type) {
                TYPE_CUSTOMER -> RequestEndPoint.GET_ALL_CUSTOMERS
                TYPE_BRAND -> RequestEndPoint.GET_ALL_BRANDS
                TYPE_VEHICLE_TYPE -> RequestEndPoint.GET_ALL_VEHICLE_TYPES
                else -> RequestEndPoint.GET_ALL_UNITS
            },
            null,
            RequestResult::getAllGeneralProperties
        ).collect{ res ->
            res.response?.data?.let { addProperties(it as List<GeneralProperty>) }
        }
    }

    private fun addProperties(list: List<GeneralProperty>) {
        exploreAdapter.setProperty(list)
    }

    override fun onItemClick(item: Any) {
        _selectedItem.postValue(HandledEvent(item as GeneralProperty))
    }

    fun setMode(searching: Boolean, type: Int) {
        this.searching = searching
        this.type = type
        viewModelScope.launch { getAllProperty(type) }
    }


}
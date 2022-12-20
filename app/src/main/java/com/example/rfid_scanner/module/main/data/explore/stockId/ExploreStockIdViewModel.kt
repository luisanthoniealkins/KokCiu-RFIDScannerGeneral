package com.example.rfid_scanner.module.main.data.explore.stockId

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.module.main.data.explore.stockId.adapter.ExploreStockIdAdapter
import com.example.rfid_scanner.module.main.data.explore.stockId.adapter.ExploreStockIdAdapter.StockIdData
import com.example.rfid_scanner.module.main.data.explore.stockId.adapter.QuantityAdapter.StockIdSelected
import com.example.rfid_scanner.utils.custom.kclass.HandledEvent
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import com.example.rfid_scanner.utils.listener.ItemClickListener
import kotlinx.coroutines.launch

class ExploreStockIdViewModel : BaseViewModel(), ItemClickListener {

    companion object {
        const val KEY_STOCK_ID = "keyStockId"
    }

    val exploreAdapter = ExploreStockIdAdapter(this)
    var searching = false

    private val _selectedItem = MutableLiveData<HandledEvent<StockIdSelected>>()
    val selectedItem : LiveData<HandledEvent<StockIdSelected>> = _selectedItem

    fun getAllStockIds() {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.GET_ALL_STOCK_IDS,
                null,
                RequestResult::getAllStockIds
            ).collect{ res ->
                res.response?.data?.let { addStockIds(it as List<StockId>) }
            }
        }
    }

    private fun addStockIds(list: List<StockId>) {
        exploreAdapter.setStockIds(
            !searching,
            list.groupBy {
                it.stock.code
            }.map {
                StockIdData(it.value.first().stock, it.value)
            }
        )
    }

    override fun onItemClick(item: Any) {
        _selectedItem.postValue(HandledEvent(item as StockIdSelected))
    }




}
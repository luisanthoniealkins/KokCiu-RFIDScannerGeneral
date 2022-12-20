package com.example.rfid_scanner.module.main.data.explore.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.Stock
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.data.model.StockRequirement
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.utils.custom.kclass.HandledEvent
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import com.example.rfid_scanner.utils.listener.ItemClickListener
import kotlinx.coroutines.launch

class ExploreStockViewModel : BaseViewModel(), ItemClickListener {

    companion object {
        const val KEY_STOCK = "keyStock"
    }

    val exploreAdapter = ExploreStockAdapter(this)
    var searching = false

    private val _selectedItem = MutableLiveData<HandledEvent<Stock>>()
    val selectedItem : LiveData<HandledEvent<Stock>> = _selectedItem

    fun getAllStocks() {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.GET_ALL_STOCKS,
                null,
                RequestResult::getAllStocks
            ).collect { res ->
                res.response?.data?.let { addStocks(it as List<StockRequirement>) }
            }
        }
    }

    private fun addStocks(rStocks: List<StockRequirement>) {
        exploreAdapter.setStocks(rStocks.map { it.stock })
    }

    override fun onItemClick(item: Any) {
        _selectedItem.postValue(HandledEvent(item as Stock))
    }
}
package com.example.rfid_scanner.module.main.data.explore.stockId

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.module.main.data.explore.stockId.adapter.ExploreStockIdAdapter
import com.example.rfid_scanner.module.main.data.explore.stockId.adapter.ExploreStockIdAdapter.StockIdData
import com.example.rfid_scanner.module.main.data.explore.stockId.adapter.QuantityAdapter.StockIdSelected
import com.example.rfid_scanner.utils.custom.kclass.HandledEvent
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import com.example.rfid_scanner.utils.listener.ItemClickListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExploreStockIdViewModel : BaseViewModel(), ItemClickListener {

    companion object {
        const val KEY_STOCK_ID = "keyStockId"
    }

    val exploreAdapter = ExploreStockIdAdapter(this)
    var searching = false

    private var lastTextFilter = "DEFAULT_TEXT"

    private val _selectedItem = MutableLiveData<HandledEvent<StockIdSelected>>()
    val selectedItem : LiveData<HandledEvent<StockIdSelected>> = _selectedItem

    private fun getAllStockIds(textFilter: String) {
        exploreAdapter.setStockIds(!searching, mutableListOf())
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.GET_ALL_STOCK_IDS,
                RequestParam.getAllStockIds(textFilter),
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

    // Input Debouncing Method
    private var searchJob: Job? = null
    fun executeGetAllStockIds(textFilter: String) {
        if (lastTextFilter == textFilter) return
        lastTextFilter = textFilter

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            getAllStockIds(textFilter)
        }
    }
}
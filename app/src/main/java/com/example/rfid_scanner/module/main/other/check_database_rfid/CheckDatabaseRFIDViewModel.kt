package com.example.rfid_scanner.module.main.other.check_database_rfid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.StockRFID
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.data.repository.component.ResponseCode
import com.example.rfid_scanner.module.main.other.check_database_rfid.adapter.StockRFIDViewHolder
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class CheckDatabaseRFIDViewModel : BaseViewModel() {

    val adapter = StockRFIDViewHolder(mutableListOf()).getAdapter()

    private val _queryState = MutableLiveData<Int>()
    val queryState : LiveData<Int> = _queryState

    fun checkDatabaseRFID() {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.VALIDATE_RFID,
                null,
                RequestResult::validateRFID,
            ).collect {
                _queryState.postValue(it.state)
                it.response?.let { data ->
                    if (data.code == ResponseCode.OK) {
                        addStockRFID(data.data as List<StockRFID>)
                    }
                }
            }
        }
    }

    private fun addStockRFID(stockRFIDS: List<StockRFID>) {
        adapter.clearData()
        stockRFIDS.map { adapter.addData(it) }
    }
}


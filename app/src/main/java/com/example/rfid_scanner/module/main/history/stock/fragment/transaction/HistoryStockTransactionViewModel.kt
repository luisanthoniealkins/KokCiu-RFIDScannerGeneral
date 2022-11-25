package com.example.rfid_scanner.module.main.history.stock.fragment.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.Transaction
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.module.main.history.stock.fragment.transaction.adapter.StockTransactionAdapter
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*

class HistoryStockTransactionViewModel : BaseViewModel() {

    private val _lvTransactionDates = MutableLiveData<List<String>>()
    val lvTransactionDates : LiveData<List<String>> = _lvTransactionDates

    val adapter = StockTransactionAdapter(mutableListOf())

    private lateinit var currentStockCode: String

    fun setStockCode(stockCode: String) {
        currentStockCode = stockCode
    }

    fun getAllTransactionDates() {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.GET_ALL_TRANSACTIONS_DATES,
                null,
                RequestResult::getAllTransactionsDates
            ).collect{ res ->
                res.response?.data?.let {
                    _lvTransactionDates.postValue(it as List<String>)
                }
            }
        }
    }

    fun getStockTransaction(selectedDate: Date) {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.GET_STOCK_TRANSACTION,
                RequestParam.getStockTransaction(currentStockCode, selectedDate),
                RequestResult::getStockTransaction
            ).collect{ res ->
                res.response?.data?.let {
                    adapter.setTransactions(it as List<Transaction>)
                }
            }
        }
    }

}
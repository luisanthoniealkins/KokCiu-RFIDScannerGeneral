package com.example.rfid_scanner.module.main.history.transaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.Transaction
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.module.main.history.transaction.adapter.TransactionAdapter
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*

class HistoryTransactionViewModel : BaseViewModel() {

    private val _lvTransactionDates = MutableLiveData<List<String>>()
    val lvTransactionDates : LiveData<List<String>> = _lvTransactionDates

    private val _lvTransactions = MutableLiveData<List<Transaction>>()
    val lvTransactions : LiveData<List<Transaction>> = _lvTransactions

    val adapter = TransactionAdapter(mutableListOf())

    fun getAllTransactions(currentDate: Date) {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.GET_ALL_TRANSACTIONS,
                RequestParam.getAllTransactions(currentDate),
                RequestResult::getAllTransactions
            ).collect{ res ->
                res.response?.data?.let {
                    _lvTransactions.postValue(it as List<Transaction>)
                }
            }
        }
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

}
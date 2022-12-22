package com.example.rfid_scanner.module.main.data.info.transaction_rfid

import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class TransactionRFIDViewModel : BaseViewModel() {

    val rfidAdapter = TransactionRFIDAdapter()

    fun setTransaction(transactionCode: String) {
        getTransactionRFIDS(transactionCode)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getTransactionRFIDS(transactionCode: String) {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.GET_TRANSACTION_RFIDS,
                RequestParam.getTransactionRFIDS(transactionCode),
                RequestResult::getTransactionRFIDS,
            ).collect { res ->
                res.response?.let {
                    rfidAdapter.setRFID(it.data as List<String>)
                }
            }
        }
    }

}
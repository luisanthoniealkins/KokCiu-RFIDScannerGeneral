package com.example.rfid_scanner.module.main.history.stock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel

class HistoryStockViewModel : BaseViewModel() {

    companion object {
        val tabHeaders = listOf("Detail", "Transaksi")
    }

    private val _lvStockCode = MutableLiveData<String>()
    val lvStockCode: LiveData<String> = _lvStockCode

    var isInitialEntry = true
    lateinit var currentStockCode: String

    fun setStockCode(stockCode: String) {
        currentStockCode = stockCode
        _lvStockCode.postValue(stockCode)
    }

}
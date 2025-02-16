package com.example.rfid_scanner.module.main.history.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.Transaction
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.module.main.history.transaction.adapter.TransactionAdapter
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.custom.kclass.HandledEvent
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import com.example.rfid_scanner.utils.helper.DateHelper
import com.example.rfid_scanner.utils.listener.ItemClickListener
import kotlinx.coroutines.launch
import java.util.*

class HistoryTransactionViewModel : BaseViewModel(), ItemClickListener {

    companion object {
        const val STATUS_MASUK = "MASUK"
        const val STATUS_KELUAR = "KELUAR"
        const val STATUS_RETUR = "RETUR"
        const val STATUS_RUSAK = "RUSAK"
        const val STATUS_HAPUS = "HAPUS"
        const val STATUS_PENYESUAIAN = "PENYESUAIAN"
        const val STATUS_TAMBAHAN = "TAMBAHAN"
    }


    val adapter = TransactionAdapter(mutableListOf(), this)

    var showFilterList = false

    private val _lvTransactionDates = MutableLiveData<List<String>>()
    val lvTransactionDates: LiveData<List<String>> = _lvTransactionDates

    private val _lvQueryInfo = MutableLiveData<String>()
    val lvQueryInfo: LiveData<String> = _lvQueryInfo

    private val _lvSelectedItem = MutableLiveData<HandledEvent<Any>>()
    val lvSelectedItem: LiveData<HandledEvent<Any>> = _lvSelectedItem

    private val _lvShowQueryAllButton = MutableLiveData<Boolean>()
    val lvShowQueryAllButton: LiveData<Boolean> = _lvShowQueryAllButton

    private val _lvQueryState = MutableLiveData<MResponse>()
    val lvQueryState: LiveData<MResponse> = _lvQueryState

    fun getAllTransactionDates() {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.GET_ALL_TRANSACTIONS_DATES,
                null,
                RequestResult::getAllTransactionsDates
            ).collect { res ->
                res.response?.data?.let {
                    _lvTransactionDates.postValue(it as List<String>)
                }
            }
        }
    }

    override fun onItemClick(item: Any) {
        _lvSelectedItem.postValue(HandledEvent(item))
    }

    private var selectedTransactionDate: Date? = null
    var checkInChecked = StorageService.getI().isTransactionFilterChecked(STATUS_MASUK, true)
    var checkOutChecked = StorageService.getI().isTransactionFilterChecked(STATUS_KELUAR, true)
    var returnChecked = StorageService.getI().isTransactionFilterChecked(STATUS_RETUR, true)
    var brokenChecked = StorageService.getI().isTransactionFilterChecked(STATUS_RUSAK, true)
    var clearChecked = StorageService.getI().isTransactionFilterChecked(STATUS_HAPUS, true)
    var adjustChecked = StorageService.getI().isTransactionFilterChecked(STATUS_PENYESUAIAN, true)
    var othersChecked = StorageService.getI().isTransactionFilterChecked(STATUS_TAMBAHAN, false)

    fun setTransactionDate(toString: String) {
        selectedTransactionDate = DateHelper.getDate("MMMM, yyyy", toString)!!

        getAllTransactions()
    }

    fun getAllTransactions(isLimited: Boolean = true) {
        _lvShowQueryAllButton.postValue(isLimited)

        adapter.setTransactions(mutableListOf())
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.GET_ALL_TRANSACTIONS,
                RequestParam.getAllTransactions(
                    selectedTransactionDate,
                    isLimited,
                    checkInChecked,
                    checkOutChecked,
                    returnChecked,
                    brokenChecked,
                    clearChecked,
                    adjustChecked,
                    othersChecked,
                ),
                RequestResult::getAllTransactions
            ).collect { res ->
                _lvQueryState.postValue(res)
                res.response?.data?.let {
                    adapter.setTransactions(it as List<Transaction>)
                    _lvQueryInfo.postValue(
                        if (isLimited)
                            "${it.size} Transaksi Terakhir"
                        else
                            "${it.size} Transaksi"
                    )
                }
            }
        }
    }

    fun setFilter(
        checkInChecked: Boolean,
        checkOutChecked: Boolean,
        returnChecked: Boolean,
        brokenChecked: Boolean,
        clearChecked: Boolean,
        adjustChecked: Boolean,
        othersChecked: Boolean
    ) {
        this.checkInChecked = checkInChecked
        this.checkOutChecked = checkOutChecked
        this.returnChecked = returnChecked
        this.brokenChecked = brokenChecked
        this.clearChecked = clearChecked
        this.adjustChecked = adjustChecked
        this.othersChecked = othersChecked

        StorageService.getI().setTransactionFilterChecked(STATUS_MASUK, checkInChecked)
        StorageService.getI().setTransactionFilterChecked(STATUS_KELUAR, checkOutChecked)
        StorageService.getI().setTransactionFilterChecked(STATUS_RETUR, returnChecked)
        StorageService.getI().setTransactionFilterChecked(STATUS_RUSAK, brokenChecked)
        StorageService.getI().setTransactionFilterChecked(STATUS_HAPUS, clearChecked)
        StorageService.getI().setTransactionFilterChecked(STATUS_PENYESUAIAN, adjustChecked)
        StorageService.getI().setTransactionFilterChecked(STATUS_TAMBAHAN, othersChecked)

        getAllTransactions()
    }

    fun getFilterCount() = listOf(
        checkInChecked,
        checkOutChecked,
        returnChecked,
        brokenChecked,
        clearChecked,
        adjustChecked,
        othersChecked
    ).count { it }

}
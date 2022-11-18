package com.example.rfid_scanner.module.main.scan.transaction.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.*
import com.example.rfid_scanner.data.model.Tag.Companion.isProperTag
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.ErrorViewHolder
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.ErrorViewHolder.Companion.addErrorTag
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.ErrorViewHolder.Companion.clearErrorTags
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.StockViewHolder
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.StockViewHolder.Companion.addOrUpdateStock
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.StockViewHolder.Companion.addTagsToStock
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.StockViewHolder.Companion.clearStockTags
import com.example.rfid_scanner.module.main.scan.transaction.general.adapter.ErrorAdapter
import com.example.rfid_scanner.module.main.scan.transaction.general.adapter.TagAdapter
import com.example.rfid_scanner.service.BluetoothScannerService
import com.example.rfid_scanner.utils.extension.StringExt.getSimilarStrings
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import com.example.rfid_scanner.utils.generic.viewmodel.ScanViewModel
import com.example.rfid_scanner.utils.helper.LogHelper
import com.example.rfid_scanner.utils.helper.TextHelper.emptyString
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class TransCheckoutViewModel : ScanViewModel() {

    companion object {
        const val TAB_STOCK = 0
        const val TAB_ERROR = 1
    }

    private val _lvBills = MutableLiveData<List<Bill>>()
    val lvBills : LiveData<List<Bill>> = _lvBills

    private val _tagCountStock = MutableLiveData<Int>()
    val tagCountStock : LiveData<Int> = _tagCountStock

    private val _tagCountError = MutableLiveData<Int>()
    val tagCountError : LiveData<Int> = _tagCountError

    private val _isVerified = MutableLiveData<Boolean>().apply { postValue(false) }
    val isVerified : LiveData<Boolean> = _isVerified

    private val _commitState = MutableLiveData<Int>()
    val commitState : LiveData<Int> = _commitState

    val stockAdapter = StockViewHolder().getAdapter()
    val errorAdapter = ErrorViewHolder().getAdapter()
    var isInitialEntry = true

    private var channelTags = Channel<List<TagEPC>>()

    private val bills = mutableListOf<Bill>()
    private var activeStocks = mutableSetOf<String>()
    private var queriedTags = mutableSetOf<String>()

    init {
        viewModelScope.launch {
            mBluetoothScannerService.setChannel(channelTags)
            launch { channelTags.consumeEach { queryTags(it) } }
//            launch { mBluetoothScannerService.sfScanStatus.collect{ _scanStatus.postValue(it) } }
        }
    }

    fun getBillCodes() = bills.map { it.billCode }

    fun getCustomerCode() = bills.firstOrNull()?.customerCode ?: emptyString()

    fun addBill(bill: Bill?) {
        if ((bill != null) && !bills.contains(bill)) {
            bills.add(bill)
            queryStocks(bill.reqStocks)
        }
        _lvBills.postValue(bills)
    }

    private fun queryStocks(stocksReq: MutableList<StockRequirement>) {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.GET_STOCKS,
                RequestParam.getStocks(stocksReq),
                RequestResult::getStocks
            ).collect{ res ->
                res.response?.data?.let { data ->
                    val stocks = data as MutableList<Stock>
                    stocks.map { stock ->
                        activeStocks.add(stock.code)
                        stockAdapter.mapOfOperations[addOrUpdateStock]?.let { func ->
                            (func as ((StockRequirement) -> Unit))(
                                StockRequirement(
                                    stock = stock,
                                    reqQuantity = stocksReq.first { it.stock.code == stock.code }.reqQuantity
                                ),
                            )
                        }
                        _tagCountStock.postValue(stockAdapter.itemCount)
                    }
                }
            }
        }
    }

    private fun queryTags(tags: List<TagEPC>) {
        viewModelScope.launch {
            tags.filter { !queriedTags.contains(it.epc) }
                .takeIf { it.isNotEmpty() }
                ?.let {
                    VolleyRepository.getI().requestAPI(
                        RequestEndPoint.GET_RFIDS,
                        RequestParam.getRFIDS(it),
                        RequestResult::getRFIDs
                    ).collect { res ->
                        res.response?.data?.let { tgs ->
                            val infoTags = tgs as List<Tag>
                            preProcessTags(infoTags)
                        }
                    }
                }
        }
    }

    private fun preProcessTags(infoTags: List<Tag>) {
        val newTags = infoTags.filter { !queriedTags.contains(it.epc) }
        newTags.map { queriedTags.add(it.epc) }
        splitTags(newTags)
    }

    private fun splitTags(tags: List<Tag>) {
        _isVerified.postValue(false)
        tags.map {
            if (it.status == Tag.STATUS_STORED && activeStocks.contains(it.stockCode) && it.epc.isProperTag()) {
                stockAdapter.mapOfOperations[addTagsToStock]?.let { func ->
                    (func as ((Tag) -> Unit))(it)
                }
            } else {
                errorAdapter.mapOfOperations[addErrorTag]?.let { func ->
                    (func as ((Tag) -> Unit))(it)
                }
                _tagCountError.postValue(errorAdapter.itemCount)
            }
        }
    }

    override fun resetTags() {
        queriedTags.clear()
        stockAdapter.mapOfOperations[clearStockTags]?.let { func -> (func as (() -> Unit))() }
        errorAdapter.mapOfOperations[clearErrorTags]?.let { func -> (func as (() -> Unit))() }
        _tagCountError.postValue(errorAdapter.itemCount)
    }

    fun getAdapterError(): String? {
        stockAdapter.getError()?.let { return it }
        errorAdapter.getError()?.let { return it }
        return null
    }

    fun submitVerifyResult(result: Boolean) {
        if (result) _isVerified.postValue(result)
        viewModelScope.launch { mBluetoothScannerService.setChannel(channelTags) }
    }

    fun getTagCount() = queriedTags.size

    fun commitTransaction() {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.TRANSACTION_GENERAL,
                RequestParam.transactionGeneral(
                    bills = bills,
                    stocks = stockAdapter.dataSet.map { it.stock },
                    statusFrom = "Gudang",
                    statusTo = "Terjual",
                ),
                RequestResult::getGeneralResponse
            ).collect {
                _commitState.postValue(it.state)
            }
        }
    }

    fun checkSimilarTags(): String {
        val list = mutableListOf<String>()
        stockAdapter.dataSet.map { list.addAll(it.stock.items) }
        return list.getSimilarStrings()
    }

}
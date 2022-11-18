package com.example.rfid_scanner.module.main.scan.transaction.adjustment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.*
import com.example.rfid_scanner.data.model.Tag.Companion.isProperTag
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.module.main.scan.transaction.adjustment.adapter.AdjustmentTagAdapter
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.ErrorViewHolder
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.StockViewHolder
import com.example.rfid_scanner.module.main.scan.transaction.general.adapter.ErrorAdapter
import com.example.rfid_scanner.utils.extension.StringExt.getSimilarStrings
import com.example.rfid_scanner.utils.listener.VerifyListener
import com.example.rfid_scanner.utils.generic.viewmodel.ScanViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class TransAdjustmentViewModel : ScanViewModel(), VerifyListener {

    companion object {
        const val TAB_TAG = 0
        const val TAB_ERROR = 1
    }

    val adjustmentTagAdapter = AdjustmentTagAdapter()
    val errorAdapter = ErrorViewHolder().getAdapter()

    private val _tagCountOK = MutableLiveData<Int>()
    val tagCountOK : LiveData<Int> = _tagCountOK

    private val _tagCountError = MutableLiveData<Int>()
    val tagCountError : LiveData<Int> = _tagCountError

    private val _isVerified = MutableLiveData<Boolean>().apply { postValue(false) }
    val isVerified : LiveData<Boolean> = _isVerified


    private val _commitState = MutableLiveData<Int>()
    val commitState : LiveData<Int> = _commitState

    var isInitialEntry = true

    private var channelTags = Channel<List<TagEPC>>()

    private var queriedTags = mutableSetOf<String>()
    private var currentStockCode = ""

    init {
        viewModelScope.launch {
            mBluetoothScannerService.setChannel(channelTags)
            launch { channelTags.consumeEach { queryTags(it) } }
        }
    }

    fun setStockCode(stockCode: String) {
        if (currentStockCode.isEmpty()) { // to make it non duplicate on listening to livedata when returning data
            currentStockCode = stockCode
            viewModelScope.launch {
                VolleyRepository.getI().requestAPI(
                    RequestEndPoint.GET_ALL_STOCK_RFIDS,
                    RequestParam.getAllStockRFIDS(stockCode),
                    RequestResult::getAllStockRFIDs
                ).collect{ res ->
                    res.response?.data?.let { data ->
                        val tags = data as MutableList<Tag>
                        adjustmentTagAdapter.addNewData(tags)
                        _tagCountOK.postValue(adjustmentTagAdapter.itemCount)
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

    private fun preProcessTags(tags: List<Tag>) {
        splitTags(
            tags.filter { !queriedTags.contains(it.epc) }
                .map {
                    queriedTags.add(it.epc)
                    it
                }
        )
    }

    private fun splitTags(tags: List<Tag>) {
        _isVerified.postValue(false)
        tags.map {
            if (it.status == Tag.STATUS_STORED && currentStockCode == it.stockCode && it.epc.isProperTag()) {
                adjustmentTagAdapter.addData(it)
                _tagCountOK.postValue(adjustmentTagAdapter.itemCount)
            } else {
                errorAdapter.mapOfOperations[ErrorViewHolder.addErrorTag]?.let { func ->
                    (func as ((Tag) -> Unit))(it)
                }
                _tagCountError.postValue(errorAdapter.itemCount)
            }
        }
    }

    override fun onVerifyBottomSheetDismiss(result: Boolean) {
        if (result) _isVerified.postValue(result)
        viewModelScope.launch { mBluetoothScannerService.setChannel(channelTags) }
    }

    override fun resetTags() {
        queriedTags.clear()
        adjustmentTagAdapter.resetData()
        errorAdapter.mapOfOperations[ErrorViewHolder.clearErrorTags]?.let { func -> (func as (() -> Unit))() }
        _tagCountOK.postValue(0)
        _tagCountError.postValue(0)
        _isVerified.postValue(false)
    }

    fun setShowingOK(b: Boolean) {
        adjustmentTagAdapter.setShowingOK(b)
    }

    fun commitTransaction() {
        viewModelScope.launch {
//            VolleyRepository.getI().requestAPI(
//                RequestEndPoint.TRANSACTION_GENERAL,
//                RequestParam.transactionGeneral(
//                    bills = bills,
//                    stocks = stockAdapter.dataSet.map { it.stock },
//                    statusFrom = "Gudang",
//                    statusTo = "Terjual",
//                ),
//                RequestResult::getGeneralResponse
//            ).collect {
//                _commitState.postValue(it.state)
//            }

            VolleyRepository.getI().requestAPI(
                RequestEndPoint.TRANSACTION_GENERAL,
                RequestParam.transactionGeneral(
                    tags = adjustmentTagAdapter.getUnScannedTags(),
                    statusFrom = "Gudang",
                    statusTo = "Hilang",
                ),
                RequestResult::getGeneralResponse
            ).collect {
                _commitState.postValue(it.state)
            }
        }
    }

    fun checkSimilarTags(): String =
        adjustmentTagAdapter.getScannedTags().map { it.epc }.getSimilarStrings()

}
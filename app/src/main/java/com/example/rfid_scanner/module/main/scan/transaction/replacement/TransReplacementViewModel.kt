package com.example.rfid_scanner.module.main.scan.transaction.replacement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.*
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_LOST
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_STORED
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_UNKNOWN
import com.example.rfid_scanner.data.model.Tag.Companion.isProperTag
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.module.main.scan.transaction.replacement.adapter.ReplacementTagAdapter
import com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter.ErrorViewHolder
import com.example.rfid_scanner.module.main.scan.transaction.general.adapter.TagAdapter
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.extension.StringExt.getSimilarStrings
import com.example.rfid_scanner.utils.extension.StringExt.getSimilarStringsTo
import com.example.rfid_scanner.utils.extension.StringExt.isSimilarTo
import com.example.rfid_scanner.utils.listener.VerifyListener
import com.example.rfid_scanner.utils.generic.viewmodel.ScanViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class TransReplacementViewModel : ScanViewModel(), VerifyListener {

    companion object {
        const val TAB_TAG = 0
        const val TAB_ERROR = 1
    }

    val replacementTagAdapter = ReplacementTagAdapter()
    val errorAdapter = ErrorViewHolder().getAdapter()

    private val _tagCountOK = MutableLiveData<Int>()
    val tagCountOK : LiveData<Int> = _tagCountOK

    private val _tagCountError = MutableLiveData<Int>()
    val tagCountError : LiveData<Int> = _tagCountError

    private val _isVerified = MutableLiveData<Boolean>().apply { postValue(false) }
    val isVerified : LiveData<Boolean> = _isVerified

    private val _commitState = MutableLiveData<Int>()
    val commitState : LiveData<Int> = _commitState

    private var channelTags = Channel<List<TagEPC>>()

    private var currentTag : Tag? = null
    private var currentStockCode = ""

    val mapOfTags = mutableMapOf<String, Tag>()
    private val mapOfTagsOK = mutableMapOf<String, TagAdapter.TagData>()
    private val mapOfTagsError = mutableMapOf<String, TagAdapter.TagData>()

    init {
        viewModelScope.launch {
            mBluetoothScannerService.setChannel(channelTags)
            launch { channelTags.consumeEach { queryTags(it) } }
        }
    }

    fun setReplacementData(stockCode: String, tag: Tag) {
        currentStockCode = stockCode
        currentTag = tag
        replacementTagAdapter.setPivotTag(tag)
    }

    private fun queryTags(tags: List<TagEPC>) {
        viewModelScope.launch {
            removeQueried(tags).takeIf { it.isNotEmpty() }?.let {
                VolleyRepository.getI().requestAPI(
                    RequestEndPoint.GET_RFIDS,
                    RequestParam.getRFIDS(removeQueried(tags)),
                    RequestResult::getRFIDs
                ).collect{ res ->
                    res.response?.data?.let { tgs ->
                        val infoTags = tgs as List<Tag>
                        addTags(infoTags)
                    }
                }
            }
        }
    }

    private fun removeQueried(tags: List<TagEPC>) : List<TagEPC> {
        val newTags = mutableListOf<TagEPC>()
        tags.map { if (!mapOfTags.containsKey(it.epc)) newTags.add(it) }
        return newTags
    }

    private fun addTags(tags: List<Tag>) {
        val newTags = mutableListOf<Tag>()
        tags.map {
            val isCreate = !mapOfTags.containsKey(it.epc)
            mapOfTags[it.epc] = it

            if (isCreate) newTags.add(it)
        }

        if (newTags.isNotEmpty()) splitNewTags(newTags)
    }

    private fun splitNewTags(tags: List<Tag>) {
        _isVerified.postValue(false)
        tags.map {
            if (it.epc.isSimilarTo(currentTag!!.epc, StorageService.getI().epcDiffTolerance)) {
                mapOfTagsOK[it.epc] = TagAdapter.TagData(mapOfTagsOK.size, it)
                mapOfTagsOK.getOrDefault(it.epc, null)?.let { adapterItem ->
                    replacementTagAdapter.updateData(true, adapterItem.position, adapterItem.data)
                }
                _tagCountOK.postValue(mapOfTagsOK.size)
            } else {
                mapOfTagsError[it.epc] = TagAdapter.TagData(mapOfTagsError.size, it)
                errorAdapter.mapOfOperations[ErrorViewHolder.addErrorTag]?.let { func ->
                    (func as ((Tag) -> Unit))(it)
                }
                _tagCountError.postValue(errorAdapter.itemCount)
            }
        }
    }

    override fun resetTags() {
        mapOfTags.clear()
        mapOfTagsOK.clear()
        mapOfTagsError.clear()
        replacementTagAdapter.clearData()
        errorAdapter.mapOfOperations[ErrorViewHolder.clearErrorTags]?.let { func -> (func as (() -> Unit))() }
        _tagCountOK.postValue(0)
        _tagCountError.postValue(0)
        _isVerified.postValue(false)
    }

    override fun onVerifyBottomSheetDismiss(result: Boolean) {
        if (result) _isVerified.postValue(result)
        viewModelScope.launch { mBluetoothScannerService.setChannel(channelTags) }
    }

    fun commitTransaction() {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.TRANSACTION_GENERAL,
                RequestParam.transactionGeneral(
                    tags = listOf(currentTag!!),
                    statusFrom = STATUS_STORED,
                    statusTo = STATUS_UNKNOWN,
                ),
                RequestResult::getGeneralResponse
            ).collect {
                if (it.state == MResponse.FINISHED_SUCCESS) {
                    VolleyRepository.getI().requestAPI(
                        RequestEndPoint.TRANSACTION_GENERAL,
                        RequestParam.transactionGeneral(
                            tags = replacementTagAdapter.getScannedTags(),
                            stockId = StockId.getStockIdFromId(currentTag!!.stockId!!),
                            statusFrom = STATUS_UNKNOWN,
                            statusTo = STATUS_STORED,
                        ),
                        RequestResult::getGeneralResponse
                    ).collect {
                        _commitState.postValue(it.state)
                    }
                }
            }
        }
    }

    fun checkSimilarTags(): String =
        mapOfTagsError.keys.toList().getSimilarStringsTo(mapOfTagsOK.keys.first())
}
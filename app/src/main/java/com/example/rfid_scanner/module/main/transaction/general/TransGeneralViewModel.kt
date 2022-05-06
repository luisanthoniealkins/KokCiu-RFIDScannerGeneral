package com.example.rfid_scanner.module.main.transaction.general

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.Stock
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_STORED
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_UNKNOWN
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.helper.RequestEndPoint
import com.example.rfid_scanner.data.repository.helper.RequestEndPoint.Companion.TRANSACTION_GENERAL
import com.example.rfid_scanner.data.repository.helper.RequestParam
import com.example.rfid_scanner.data.repository.helper.RequestParam.transactionGeneral
import com.example.rfid_scanner.data.repository.helper.RequestResult
import com.example.rfid_scanner.data.repository.helper.RequestResult.Companion.getGeneralResponse
import com.example.rfid_scanner.module.main.transaction.general.adapter.ErrorAdapter
import com.example.rfid_scanner.module.main.transaction.general.adapter.TagAdapter
import com.example.rfid_scanner.module.main.transaction.general.adapter.TagAdapter.TagData
import com.example.rfid_scanner.module.main.transaction.general.verify.VerifyInterface
import com.example.rfid_scanner.utils.generic.BaseViewModel
import com.example.rfid_scanner.service.BluetoothScannerService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class TransGeneralViewModel : BaseViewModel(), VerifyInterface {

    companion object {
        const val TAB_TAG = 0
        const val TAB_ERROR = 1
    }

    val mBluetoothScannerService = BluetoothScannerService.getInstance()
    val tagAdapter = TagAdapter()
    val errorAdapter = ErrorAdapter()

    private var imStatusForm = STATUS_UNKNOWN

    private val _statusFrom = MutableLiveData<String>().apply { postValue(imStatusForm) }
    val statusFrom : LiveData<String> = _statusFrom

    private val _statusTo = MutableLiveData<String>().apply { postValue(STATUS_STORED) }
    val statusTo : LiveData<String> = _statusTo

    private val _tagCountOK = MutableLiveData<Int>()
    val tagCountOK : LiveData<Int> = _tagCountOK

    private val _tagCountError = MutableLiveData<Int>()
    val tagCountError : LiveData<Int> = _tagCountError

    private val _scanStatus = MutableLiveData<ScanStatus>()
    val scanStatus : LiveData<ScanStatus> = _scanStatus

    private val _isVerified = MutableLiveData<Boolean>().apply { postValue(false) }
    val isVerified : LiveData<Boolean> = _isVerified

    private val _commitState = MutableLiveData<Int>()
    val commitState : LiveData<Int> = _commitState

    val mapOfTags = mutableMapOf<String, Tag>()
    private val mapOfTagsOK = mutableMapOf<String, TagData>()
    private val mapOfTagsError = mutableMapOf<String, TagData>()

    private var channelTags = Channel<List<TagEPC>>()

    init {
        viewModelScope.launch {
            mBluetoothScannerService.setChannel(channelTags)
            launch { channelTags.consumeEach { queryTags(it) } }
            launch { mBluetoothScannerService.sfScanStatus.collect{ _scanStatus.postValue(it) } }
        }
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
            if (it.status == imStatusForm) {
                mapOfTagsOK[it.epc] = TagData(mapOfTagsOK.size, it)
                mapOfTagsOK.getOrDefault(it.epc, null)?.let { adapterItem ->
                    tagAdapter.updateData(true, adapterItem.position, adapterItem.data)
                }
                _tagCountOK.postValue(mapOfTagsOK.size)
            } else {
                mapOfTagsError[it.epc] = TagData(mapOfTagsError.size, it)
                mapOfTagsError.getOrDefault(it.epc, null)?.let { adapterItem ->
                    errorAdapter.updateData(true, adapterItem.position, adapterItem.data)
                }
                _tagCountError.postValue(mapOfTagsError.size)
            }
        }
    }

    private fun refreshNewTagsStatus() {
        clearAdapterTags()
        splitNewTags(mapOfTags.map { it.value })
    }

    fun clearTags() {
        mapOfTags.clear()
        clearAdapterTags()
        _isVerified.postValue(false)
    }

    private fun clearAdapterTags() {
        tagAdapter.clearData()
        errorAdapter.clearData()
        mapOfTagsOK.clear()
        mapOfTagsError.clear()
        _tagCountOK.postValue(0)
        _tagCountError.postValue(0)
    }

    fun setStatusButton(isSource: Boolean, status: String) {
        if (isSource) {
            _statusFrom.postValue(status)
            if (imStatusForm != status) {
                imStatusForm = status
                refreshNewTagsStatus()
            }
        }
        else _statusTo.postValue(status)
    }

    override fun onBottomSheetDismiss(result: Boolean) {
        if (result) _isVerified.postValue(result)
        viewModelScope.launch { mBluetoothScannerService.setChannel(channelTags) }
    }

    fun commitTransaction() {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                TRANSACTION_GENERAL,
                transactionGeneral(
                    bills = null,
                    stocks = null,
                    stockId = StockId(
                        Stock("12020-42000 HYK"),
                        "12020-42000 HYK#Q1",
                        1
                    ),
                    tags = mapOfTags.map { it.value },
                    statusFrom = statusFrom.value!!,
                    statusTo = statusTo.value!!,
                ),
                ::getGeneralResponse
            ).collect {
                _commitState.postValue(it.state)
            }
        }
    }


}
package com.example.rfid_scanner.module.main.scan.transaction.adjust

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_BROKEN
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_SOLD
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_STORED
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_UNKNOWN
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestEndPoint.Companion.TRANSACTION_GENERAL
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestParam.transactionGeneral
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.data.repository.component.RequestResult.getGeneralResponse
import com.example.rfid_scanner.module.main.scan.transaction.general.adapter.ErrorAdapter
import com.example.rfid_scanner.module.main.scan.transaction.general.adapter.TagAdapter
import com.example.rfid_scanner.module.main.scan.transaction.general.adapter.TagAdapter.TagData
import com.example.rfid_scanner.utils.listener.VerifyListener
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.generic.viewmodel.ScanViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class TransAdjustmentViewModel : ScanViewModel(), VerifyListener {

    companion object {
        const val TAB_TAG = 0
        const val TAB_ERROR = 1

        const val GENERAL = "General"
        const val CHECK_IN = "Masuk Baru"
        const val RETURN = "Masuk Lama"
        const val BROKEN = "Keluar Rusak"
        const val CLEAR = "Hapus Tag"
        const val REUSE = "Pakai Ulang"
        val mapOfTransType = mapOf(
            GENERAL to Pair(STATUS_UNKNOWN, STATUS_STORED),
            CHECK_IN to Pair(STATUS_UNKNOWN, STATUS_STORED),
            RETURN to Pair(STATUS_SOLD, STATUS_STORED),
            BROKEN to Pair(STATUS_STORED, STATUS_BROKEN),
            CLEAR to Pair(STATUS_STORED, STATUS_UNKNOWN),
            REUSE to Pair(STATUS_SOLD, STATUS_UNKNOWN),
        )
    }

    val tagAdapter = TagAdapter()
    val errorAdapter = ErrorAdapter()

    private val _statusFrom = MutableLiveData<String>().apply { value = STATUS_UNKNOWN }
    val statusFrom : LiveData<String> = _statusFrom

    private val _statusTo = MutableLiveData<String>().apply { value = STATUS_STORED }
    val statusTo : LiveData<String> = _statusTo

    private val _allowTrans = MutableLiveData<Boolean>().apply { value = true }
    val allowTrans : LiveData<Boolean> = _allowTrans

    private val _tagCountOK = MutableLiveData<Int>()
    val tagCountOK : LiveData<Int> = _tagCountOK

    private val _tagCountError = MutableLiveData<Int>()
    val tagCountError : LiveData<Int> = _tagCountError

    private val _isVerified = MutableLiveData<Boolean>().apply { postValue(false) }
    val isVerified : LiveData<Boolean> = _isVerified

    private val _commitState = MutableLiveData<Int>()
    val commitState : LiveData<Int> = _commitState

    val mapOfTags = mutableMapOf<String, Tag>()
    private val mapOfTagsOK = mutableMapOf<String, TagData>()
    private val mapOfTagsError = mutableMapOf<String, TagData>()

    private var channelTags = Channel<List<TagEPC>>()

    var stockId : StockId? = null
        private set

    init {
        viewModelScope.launch {
            mBluetoothScannerService.setChannel(channelTags)
            launch { channelTags.consumeEach { queryTags(it) } }
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
            if (it.status == _statusFrom.value && it.epc.isNotEmpty()) {
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
            val statusFrom = _statusFrom.value!!
            _statusFrom.value = status
            if (statusFrom != status) { refreshNewTagsStatus() }
        }
        else _statusTo.value = status
        _allowTrans.value = StorageService.getI().isStatusChecked(_statusFrom.value!!, _statusTo.value!!)
    }

    override fun onVerifyBottomSheetDismiss(result: Boolean) {
        if (result) _isVerified.postValue(result)
        viewModelScope.launch { mBluetoothScannerService.setChannel(channelTags) }
    }

    fun commitTransaction() {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                TRANSACTION_GENERAL,
                transactionGeneral(
                    stockId = stockId,
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

    fun selectStockId(stockId: StockId) {
        this.stockId = stockId
    }

    fun setTransaction(transactionType: String) {
        val pair = mapOfTransType[transactionType]
        _statusFrom.value = pair?.first
        _statusTo.value = pair?.second
    }


}
package com.example.rfid_scanner.module.main.scan.non_transaction.check_room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.StockRequirement
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_STORED
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.module.main.scan.transaction.general.adapter.ErrorAdapter
import com.example.rfid_scanner.module.main.scan.transaction.general.adapter.TagAdapter
import com.example.rfid_scanner.service.BluetoothScannerService
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import com.example.rfid_scanner.utils.generic.viewmodel.ScanViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class CheckRoomViewModel : ScanViewModel() {

    val stockAdapter = StockAdapter()
    val errorAdapter = ErrorAdapter()

    private val _stockCount = MutableLiveData<Int>()
    val stockCount : LiveData<Int> = _stockCount

    private val _tagCountError = MutableLiveData<Int>()
    val tagErrorCount : LiveData<Int> = _tagCountError

    private val setOfTags = mutableSetOf<String>()
    private val mapOfTagsError = mutableMapOf<String, TagAdapter.TagData>()

    private val channelTags = Channel<List<TagEPC>>()

    init {
        viewModelScope.launch {
            launch { getAllStocks() }
            mBluetoothScannerService.setChannel(channelTags)
            launch { channelTags.consumeEach { queryTags(it) } }
        }
    }

    private suspend fun getAllStocks() {
        VolleyRepository.getI().requestAPI(
            RequestEndPoint.GET_ALL_STOCKS,
            null,
            RequestResult::getAllStocks
        ).collect { res ->
            res.response?.data?.let { stockAdapter.addStocks(it as List<StockRequirement>) }
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
        tags.map { if (!setOfTags.contains(it.epc)) newTags.add(it) }
        return newTags
    }

    private fun addTags(tags: List<Tag>) {
        val newTags = mutableListOf<Tag>()
        tags.map {
            val isCreate = !setOfTags.contains(it.epc)

            if (isCreate) {
                setOfTags.add(it.epc)
                newTags.add(it)
            }
        }

        if (newTags.isNotEmpty()) splitNewTags(newTags)
    }

    private fun splitNewTags(tags: List<Tag>) {
        val okTags = mutableListOf<Tag>()
        tags.map {
            if (it.status == STATUS_STORED) {
                okTags.add(it)
            } else {
                mapOfTagsError[it.epc] = TagAdapter.TagData(mapOfTagsError.size, it)
                mapOfTagsError.getOrDefault(it.epc, null)?.let { adapterItem ->
                    errorAdapter.updateData(true, adapterItem.position, adapterItem.data)
                }
            }
        }
        stockAdapter.addItems(okTags)
        refreshCount()
    }

    fun clearTags() {
        stockAdapter.clearTags()
        errorAdapter.clearData()
        setOfTags.clear()
        mapOfTagsError.clear()
        refreshCount()
    }

    fun refreshCount() {
        _stockCount.postValue(stockAdapter.itemCount)
        _tagCountError.postValue(errorAdapter.itemCount)
    }

}


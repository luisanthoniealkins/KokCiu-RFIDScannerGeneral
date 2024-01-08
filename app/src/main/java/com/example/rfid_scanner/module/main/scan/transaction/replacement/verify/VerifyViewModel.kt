package com.example.rfid_scanner.module.main.scan.transaction.replacement.verify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.module.main.scan.transaction.general.adapter.ErrorAdapter
import com.example.rfid_scanner.module.main.scan.transaction.general.adapter.TagAdapter
import com.example.rfid_scanner.module.main.scan.transaction.replacement.verify.adapter.VerifyTagAdapter
import com.example.rfid_scanner.service.BluetoothScannerService
import com.example.rfid_scanner.utils.extension.StringExt.getSimilarStringsTo
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class VerifyViewModel : BaseViewModel() {

    val mBluetoothScannerService = BluetoothScannerService.getInstance()
    lateinit var verifyTagAdapter : VerifyTagAdapter
    lateinit var errorAdapter : ErrorAdapter

    private val _tagCountOK = MutableLiveData<Int>()
    val tagCountOK : LiveData<Int> = _tagCountOK

    private val _tagCountError = MutableLiveData<Int>()
    val tagCountError : LiveData<Int> = _tagCountError

    private val _scanStatus = MutableLiveData<ScanStatus>()
    val scanStatus : LiveData<ScanStatus> = _scanStatus

    private val setOfOriginalTags = mutableSetOf<TagEPC>()
    private val mapOfTags = mutableMapOf<String, Tag>()
    private val mapOfTagsError = mutableMapOf<String, TagAdapter.TagData>()

    private val channelTags = Channel<List<TagEPC>>()

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
                    RequestParam.getRFIDS(it),
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

        splitNewTags(newTags)
    }

    private fun splitNewTags(tags: List<Tag>) {
        val okTags = mutableListOf<Tag>()
        tags.map {
            if (setOfOriginalTags.contains(TagEPC(it.epc))) {
                okTags.add(it)
            } else {
                mapOfTagsError[it.epc] = TagAdapter.TagData(mapOfTagsError.size, it)
                mapOfTagsError.getOrDefault(it.epc, null)?.let { adapterItem ->
                    errorAdapter.updateData(true, adapterItem.position, adapterItem.data)
                }
                _tagCountError.postValue(mapOfTagsError.size)
            }
        }
        verifyTagAdapter.addData(okTags)
        _tagCountOK.postValue(verifyTagAdapter.itemCount)
    }

    fun clearTags() {
        verifyTagAdapter.resetData()
        errorAdapter.clearData()
        mapOfTags.clear()
        mapOfTagsError.clear()
        _tagCountOK.postValue(verifyTagAdapter.itemCount)
        _tagCountError.postValue(0)
    }

    fun setAdapter(verifyTags: List<Tag>) {
        verifyTags.map { setOfOriginalTags.add(TagEPC(it.epc)) }
        verifyTagAdapter = VerifyTagAdapter(verifyTags.map {
            VerifyTagAdapter.VerifyTagData(
                it,
                false
            )
        })
        errorAdapter = ErrorAdapter()
        _tagCountOK.postValue(verifyTagAdapter.itemCount)
    }

    fun checkSimilarTags(): String =
        mapOfTagsError.keys.toList().getSimilarStringsTo(setOfOriginalTags.first().epc)

}
package com.example.rfid_scanner.module.main.scan.non_transaction.tag_scanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.model.TagEPCQty
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.module.main.scan.non_transaction.tag_scanner.TagScannerAdapter.TagScannerData
import com.example.rfid_scanner.service.BluetoothScannerService
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel
import com.example.rfid_scanner.utils.generic.viewmodel.ScanViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class TagScannerViewModel : ScanViewModel() {

    private val _tagCount = MutableLiveData<Int>()
    val tagCount : LiveData<Int> = _tagCount

    val adapter = TagScannerAdapter()

    private val mapOfTags = mutableMapOf<String, TagScannerData>()
    private val channelTags = Channel<List<TagEPC>>()

    init {
        viewModelScope.launch {
            mBluetoothScannerService.setChannel(channelTags)
            launch { channelTags.consumeEach { addTags(it) } }
        }
    }

    private fun addTags(tags: List<TagEPC>) {
        tags.map {

            val isCreate = !mapOfTags.containsKey(it.epc)
            mapOfTags.getOrPut(it.epc) {
                TagScannerData(mapOfTags.size, TagEPCQty(it.epc, 0))
            }.data.apply {
                quantity += 1
            }

            mapOfTags.getOrDefault(it.epc, null)?.let { adapterItem ->
                adapter.updateData(isCreate, adapterItem.position, adapterItem.data)
            }

            if (isCreate) _tagCount.postValue(mapOfTags.size)
        }
    }

    fun clearTags() {
        mapOfTags.clear()
        _tagCount.postValue(0)
        adapter.clearData()
    }

}


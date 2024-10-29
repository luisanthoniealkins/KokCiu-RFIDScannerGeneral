package com.example.rfid_scanner.module.main.scan.transaction.adjustment_rfid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.data.repository.component.ResponseCode
import com.example.rfid_scanner.utils.generic.viewmodel.ScanViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class AdjustmentDatabaseRFIDViewModel : ScanViewModel() {

    private val _lvLatestEPC = MutableLiveData<String>()
    val lvLatestEPC : LiveData<String> = _lvLatestEPC

    private val _commitState = MutableLiveData<Int>()
    val commitState : LiveData<Int> = _commitState

    private var channelTags = Channel<List<TagEPC>>()

    init {
        viewModelScope.launch {
            mBluetoothScannerService.setChannel(channelTags)
            launch { channelTags.consumeEach { addTags(it) } }
        }
    }

    private fun addTags(tags: List<TagEPC>) {
        tags.map { _lvLatestEPC.postValue(it.epc) }
    }

    override fun resetTags() {
        _lvLatestEPC.postValue("")
    }

    fun commit(stockCode: String, stockUnitCount: Int, rfidTag: String) {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.ADJUST_RFID,
                RequestParam.adjustRFID(
                    stockCode = stockCode,
                    stockUnitCount = stockUnitCount,
                    rfidCode = rfidTag,
                ),
                RequestResult::getGeneralResponse
            ).collect {
                _commitState.postValue(it.state)
                if (it.response?.code != ResponseCode.OK) {
                    it.response?.let { it1 -> showToast(it1.message) }
                }
            }
        }
    }

}
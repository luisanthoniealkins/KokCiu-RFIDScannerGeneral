package com.example.rfid_scanner.module.main.scan.non_transaction.check_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.model.Transaction
import com.example.rfid_scanner.data.repository.VolleyRepository
import com.example.rfid_scanner.data.repository.component.RequestEndPoint
import com.example.rfid_scanner.data.repository.component.RequestParam
import com.example.rfid_scanner.data.repository.component.RequestResult
import com.example.rfid_scanner.data.repository.component.ResponseCode
import com.example.rfid_scanner.utils.generic.viewmodel.ScanViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class CheckTagDetailViewModel : ScanViewModel() {

    companion object {
        val JSON_KEY = arrayOf("rfid", "stock", "check_in", "check_out", "return", "broken", "lost")
        const val dateFormat = "yyyy-MM-dd / hh:mm"
    }

    val transactions = mutableListOf<Transaction>()

    var sortAscending = false
    var isSearching = false
    var currentSearchTag = ""

    var tagScanned = false

    private val _lvTagScanned = MutableLiveData<String>()
    val lvTagScanned : LiveData<String> = _lvTagScanned

    private val _lvViews = MutableLiveData<HashMap<String,HashMap<String,String>>>()
    val lvViews : LiveData<HashMap<String,HashMap<String,String>>> = _lvViews

    private val channelTags = Channel<List<TagEPC>>()

    init {
        viewModelScope.launch {
            mBluetoothScannerService.setChannel(channelTags)
            launch { channelTags.consumeEach { addTags(it) } }
        }
    }

    fun setSearchTag(searchTag: String?) {
        searchTag?.let {
            currentSearchTag = it
            isSearching = true
        }
    }

    private fun addTags(tags: List<TagEPC>) {
        if (tagScanned || tags.isEmpty()) return
        tagScanned = true

        mBluetoothScannerService.stopScan()

        _lvTagScanned.postValue(tags.first().epc)
        getRFIDDetail(tags.first().epc)
    }

    private fun getRFIDDetail(epc: String) {
        viewModelScope.launch {
            VolleyRepository.getI().requestAPI(
                RequestEndPoint.GET_RFID_DETAIL,
                RequestParam.getRFIDDetail(epc),
                RequestResult::getRFIDDetail,
            ).collect {
                it.response?.let { data ->
                    if (data.code == ResponseCode.OK)
                        _lvViews.postValue(data.data as HashMap<String,HashMap<String,String>>)
                }
            }
        }
    }
}


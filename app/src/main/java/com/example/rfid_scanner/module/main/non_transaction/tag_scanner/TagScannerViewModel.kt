package com.example.rfid_scanner.module.main.non_transaction.tag_scanner

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.model.TagEPCQty
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.module.main.non_transaction.tag_scanner.TagScannerAdapter.TagScannerData
import com.example.rfid_scanner.utils.generic.BaseViewModel
import com.example.rfid_scanner.utils.service.BluetoothScannerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class TagScannerViewModel : BaseViewModel() {

    private val mapOfTags = mutableMapOf<String, TagScannerData>()
    val adapter = TagScannerAdapter()

    private val _tagCount = MutableLiveData<Int>()
    val tagCount : LiveData<Int> = _tagCount

    private val _scanStatus = MutableLiveData<ScanStatus>()
    val scanStatus : LiveData<ScanStatus> = _scanStatus

    val mBluetoothScannerService = BluetoothScannerService.getInstance()

    init {
        Log.d("1234567----", mBluetoothScannerService.ldTags.hasObservers().toString())
        mBluetoothScannerService.ldTags.observeForever {
            Log.d("1234567----", it.hasBeenHandled().toString())
            it.contentIfNotHandled?.let { tags ->
                addTags(tags)
            }
//            it.contentIfNotHandled?.let { tags -> addTags(tags) }
        }



        mBluetoothScannerService.sfScanStatus.observeForever{ _scanStatus.postValue(it) }

    }

    private fun addTags(tags: List<TagEPC>) {
//        Log.d("123456-", Thread.currentThread().name.toString())
        Log.d("12345----", "0111111")
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
            Log.d("123456-", _tagCount.value.toString())
            Log.d("123456------------", tagCount.value.toString())
            Log.d("12345----", mapOfTags.size.toString())
            Log.d("12345----", Thread.currentThread().name.toString())
        }
    }

    fun clearTags() {
        mapOfTags.clear()
        _tagCount.postValue(0)
        adapter.clearData()
    }

}


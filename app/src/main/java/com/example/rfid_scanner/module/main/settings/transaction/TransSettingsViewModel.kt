package com.example.rfid_scanner.module.main.settings.transaction

import android.util.Log
import com.example.rfid_scanner.data.model.Tag.Companion.statusList
import com.example.rfid_scanner.module.main.settings.transaction.AllowedTransAdapter.AllowedTransData
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.generic.BaseViewModel

class TransSettingsViewModel : BaseViewModel() {

    val adapter = AllowedTransAdapter()

    init {
        val list = mutableListOf<AllowedTransData>()
        for (from in statusList) for(to in statusList) {
            list.add(AllowedTransData(from, to, StorageService.getI().isStatusChecked(from, to)))
        }
        adapter.setData(list)
    }

    fun confirm() {
        adapter.getData().map {
            Log.d("12345-", "${it.from} ${it.to} ${it.isChecked}")
            StorageService.getI().setStatusChecked(it.from, it.to, it.isChecked)
        }
    }


}
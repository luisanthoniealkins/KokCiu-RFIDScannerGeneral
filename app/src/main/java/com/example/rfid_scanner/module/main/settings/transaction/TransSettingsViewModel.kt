package com.example.rfid_scanner.module.main.settings.transaction

import com.example.rfid_scanner.data.model.Tag.Companion.statusList
import com.example.rfid_scanner.module.main.settings.transaction.AllowedTransAdapter.AllowedTransData
import com.example.rfid_scanner.service.StorageService

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
            StorageService.getI().setStatusChecked(it.from, it.to, it.isChecked)
        }
    }
}
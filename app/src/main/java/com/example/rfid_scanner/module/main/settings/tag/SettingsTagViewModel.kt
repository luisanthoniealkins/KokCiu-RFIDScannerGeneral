package com.example.rfid_scanner.module.main.settings.tag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel

class SettingsTagViewModel : BaseViewModel() {

    private val _lvMinEPCLength = MutableLiveData<Int>().apply { postValue(StorageService.getI().minEPCLength) }
    val lvMinEPCLength : LiveData<Int> = _lvMinEPCLength

    private val _lvMaxEPCLength = MutableLiveData<Int>().apply { postValue(StorageService.getI().maxEPCLength) }
    val lvMaxEPCLength : LiveData<Int> = _lvMaxEPCLength

    private val _lvEPCDiffTolerance = MutableLiveData<Int>().apply { postValue(StorageService.getI().epcDiffTolerance) }
    val lvEPCDiffTolerance : LiveData<Int> = _lvEPCDiffTolerance

    fun updateSP(minEPCLength: Int, maxEPCLength: Int, epcDiffTolerance: Int) {
        StorageService.getI().minEPCLength = minEPCLength
        StorageService.getI().maxEPCLength = maxEPCLength
        StorageService.getI().epcDiffTolerance = epcDiffTolerance
    }


}
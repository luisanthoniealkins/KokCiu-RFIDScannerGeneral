package com.example.rfid_scanner.module.main.alter.property

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.data.model.GeneralProperty
import com.example.rfid_scanner.utils.generic.BaseViewModel

class AlterPropertyViewModel : BaseViewModel() {

    var type: Int = 0
    var property: GeneralProperty? = null
    var isCreate = false

    fun setMode(type: Int, property: GeneralProperty?) {
        this.type = type
        this.property = property
        isCreate = property == null
    }


}
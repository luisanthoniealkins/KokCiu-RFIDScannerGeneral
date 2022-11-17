package com.example.rfid_scanner.module.main.data.alter.property

import com.example.rfid_scanner.data.model.GeneralProperty
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel

class AlterPropertyViewModel : BaseViewModel() {

    var type: Int = 0
    var property: GeneralProperty? = null
    var isCreate = false

    fun setMode(type: Int, property: GeneralProperty?) {
        this.type = type
        this.property = property
        isCreate = (property == null)
    }


}
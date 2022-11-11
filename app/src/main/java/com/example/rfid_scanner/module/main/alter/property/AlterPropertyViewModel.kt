package com.example.rfid_scanner.module.main.alter.property

import com.example.rfid_scanner.data.model.GeneralProperty

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
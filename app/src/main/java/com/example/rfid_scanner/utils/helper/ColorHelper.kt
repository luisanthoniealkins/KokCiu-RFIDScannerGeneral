package com.example.rfid_scanner.utils.helper

import androidx.core.content.res.ResourcesCompat
import com.example.rfid_scanner.utils.app.App

object ColorHelper {

    fun gColor(resColorId: Int) : Int{
        return ResourcesCompat.getColor(App.res!!, resColorId, null)
    }

}
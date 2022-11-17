package com.example.rfid_scanner.module.template.fragment

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TemplateViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is template Fragment"
    }
    val text: LiveData<String> = _text

    init {
        Handler(Looper.getMainLooper()).postDelayed({
            //Do something after 100ms
        }, 100)
    }

}
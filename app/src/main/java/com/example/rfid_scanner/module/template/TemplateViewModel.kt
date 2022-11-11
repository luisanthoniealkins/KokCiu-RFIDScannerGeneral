package com.example.rfid_scanner.module.template

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TemplateViewModel : BaseViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    init {
        Handler(Looper.getMainLooper()).postDelayed({
            _text.postValue("Halo")
        }, 5000) // diplay "Halo" after 5 seconds
    }

}
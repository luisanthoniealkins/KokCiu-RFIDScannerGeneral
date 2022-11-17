package com.example.rfid_scanner.module.template.fragment.base
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.utils.generic.viewmodel.BaseViewModel

class TemplateViewModel : BaseViewModel() {

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
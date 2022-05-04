package com.example.rfid_scanner.utils.generic

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class GenericObserver {

    companion object {
        fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
            observeForever(object : Observer<T> {
                override fun onChanged(t: T?) {
                    observer.onChanged(t)
                    removeObserver(this)
                }
            })
        }
    }
    
}
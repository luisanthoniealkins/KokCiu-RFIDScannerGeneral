package com.example.rfid_scanner.utils.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

object LiveDataExt {

    fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
        observeForever(object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

}
package com.laalkins.bluetoothgeneralcontroller.utils.app

import android.app.Application
import android.content.res.Resources


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        res = resources
    }

    companion object {
        var instance: App? = null
            private set
        var res: Resources? = null
            private set
    }
}
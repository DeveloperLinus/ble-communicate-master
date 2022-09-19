package com.ble.communicate

import android.app.Application
import android.content.Context

class BleApplication : Application()  {
    companion object {
        @JvmField
        var context: Context? = null
    }

    override fun onCreate() {
        context = this
        super.onCreate()
    }
}
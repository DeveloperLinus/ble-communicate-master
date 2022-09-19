package com.ble.commonlib.base

import android.app.Application
import android.content.Context

open class BaseApplication : Application() {
    companion object {
        @JvmField
        var context: Context? = null
    }

    override fun onCreate() {
        context = this
        super.onCreate()
    }
}
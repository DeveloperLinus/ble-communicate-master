package com.ble.communicate

import android.app.Application
import android.content.Context
import com.ble.commonlib.base.Repository

class BleApplication : Application()  {
    companion object {
        @JvmField
        var context: Context? = null
    }

    override fun onCreate() {
        context = this
        super.onCreate()
        //初始化数据库
        Repository.init(this)
    }
}
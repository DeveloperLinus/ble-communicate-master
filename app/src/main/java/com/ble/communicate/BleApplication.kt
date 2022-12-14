package com.ble.communicate

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.ble.commonlib.base.RapidSPRepository
import com.ble.roomlib.db.RoomRepository

class BleApplication : Application()  {
    companion object {
        @JvmField
        var context: Context? = null
    }

    override fun onCreate() {
        context = this
        super.onCreate()
        MultiDex.install(this);
        //初始化数据库
        RapidSPRepository.init(this)
        RoomRepository.init(this)
    }
}
package com.ble.communicate.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.ble.commonlib.base.log
import com.ble.communicate.core.BleManager
import com.ble.communicate.ui.EngineerActivity
import java.lang.Exception

// 蓝牙服务
class BleMaintainService : Service() {
    companion object {
        @JvmStatic
        fun startService(context: Context) {
            log("开启蓝牙服务")
            val intent = Intent(context, BleMaintainService::class.java)
            context.startService(intent)
        }

        fun stopService(context: Context) {
            log("停止蓝牙服务")
            val intent = Intent(context, BleMaintainService::class.java)
            context.startService(intent)
        }
    }

    private lateinit var mBleManager: BleManager

    override fun onCreate() {
        mBleManager = BleManager(this) {
            return@BleManager EngineerActivity.startActivity(this)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mBleManager.startAdvertising()
        return START_STICKY
    }

    override fun onDestroy() {
        log("支持服务onDestroy")
        try {
            mBleManager.clear()
        } catch (exception: Exception) {
            log("报错${exception.message}")
        }
        super.onDestroy()
    }
    override fun onBind(p0: Intent?): IBinder? = null
}
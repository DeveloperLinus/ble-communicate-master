package com.ble.commonlib.core.wifi

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import com.ble.commonlib.base.log
import java.lang.reflect.InvocationTargetException

class WifiManager(val context: Context) {
    private val mWifiManager: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    companion object {

    }

    // 开启热点
    fun setWifiApEnabled(enabled: Boolean, ssid: String, pwd: String) : Boolean {
        log("setWifiApEnabled() called wifi: enabled->$enabled, ssid->$ssid, pwd->$pwd")
        if (enabled) {
            // wifi和热点不能同时打开，所以打开任店时需要关闭wifi
            mWifiManager.isWifiEnabled = false
        } else {
        }
        return try {
            val apConfig = WifiConfiguration() // 热点配置类
            apConfig.SSID = ssid
            // 通过反射调用设置热点
            val method = mWifiManager.javaClass.getMethod(
                "setWifiApEnabled", WifiConfiguration::class.java, java.lang.Boolean.TYPE)
            // 返回热点的打开状态
            method.invoke(mWifiManager, apConfig, enabled) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun closeWifiHotspot() {
        try {
            val method = mWifiManager.javaClass.getMethod("getWifiApConfiguration")
            method.isAccessible = true
            val config = method.invoke(mWifiManager) as WifiConfiguration
            val method2 = mWifiManager.javaClass.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
            method2.invoke(mWifiManager, config, false)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    private fun openWifi() {
        if (!mWifiManager.isWifiEnabled) {
            mWifiManager.isWifiEnabled = true
        }
    }

    // 设置WIFI
    fun connBackupsWifi() {
    }
}
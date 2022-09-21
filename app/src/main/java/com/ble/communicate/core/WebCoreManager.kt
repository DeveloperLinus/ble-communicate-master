package com.ble.communicate.core

import android.content.Context
import com.ble.commonlib.base.log
import com.ble.commonlib.utils.NetUtils
import com.ble.communicate.BleApplication
import com.yanzhenjie.andserver.AndServer
import com.yanzhenjie.andserver.Server
import java.lang.Exception
import java.net.InetAddress
import java.util.concurrent.TimeUnit

class WebCoreManager() {
    companion object {
        const val hostIp = "192.168.43.1"
        val instance : WebCoreManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { WebCoreManager() }
    }

    private lateinit var mServer: Server
    var mListener: WebServerListener? = null

    init {
        val ipAddress = NetUtils.getIpAddress(true)
        log("获取到的ip地址为:$ipAddress")
        mServer = AndServer.serverBuilder(BleApplication.context!!)
            .inetAddress(InetAddress.getByName(NetUtils.getIpAddress(true))) // 这里，如果要使用热点的ip,192.168.43.1，则传入hostId，若要使用postman进行接口测试，可以传入真实的ip
            .port(8080)
            .timeout(10, TimeUnit.SECONDS)
            .listener(object: Server.ServerListener {
                override fun onException(e: Exception?) {
                    log("WEB服务开启失败->${e?.message}")
                    mListener?.onException(exception = e!!)
                }

                override fun onStarted() {
                    log("WEB服务启动成功${mListener == null}")
                    mListener?.onStarted(mServer.inetAddress.hostAddress)
                }

                override fun onStopped() {
                    log("WEB服务启动成功")
                    mListener?.onStopped()
                }
            })
            .build()
    }

    fun start() {
        log("正在开启服务${mServer.isRunning}")
        if (mServer.isRunning) {
            mListener?.onStarted(hostIp)
        } else {
            mServer.startup()
        }
    }

    fun stop() {
        mServer.shutdown()
    }

    interface WebServerListener {
        fun onException(exception: Exception)

        fun onStarted(ip: String)

        fun onStopped()
    }
}
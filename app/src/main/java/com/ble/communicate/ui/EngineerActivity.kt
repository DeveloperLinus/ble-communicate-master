package com.ble.communicate.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.ScrollView
import com.ble.commonlib.base.BaseBindingActivity
import com.ble.commonlib.base.log
import com.ble.commonlib.core.utils.AppExecutors
import com.ble.commonlib.core.wifi.WifiManager
import com.ble.commonlib.utils.NetUtils
import com.ble.communicate.core.WebCoreManager
import com.ble.communicate.R
import com.ble.communicate.core.EngineerLogEvent
import com.ble.communicate.core.EngineerWebEvent
import com.ble.communicate.core.EngnieerConfig
import com.ble.communicate.databinding.ActivityEngineerBinding
import com.ble.communicate.service.BleMaintainService
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.util.*

class EngineerActivity : BaseBindingActivity<ActivityEngineerBinding>(), CoroutineScope by MainScope() {
    private lateinit var mPaw: String
    private lateinit var padID: String
    private lateinit var mWebManager: WebCoreManager // WEB服务
    private lateinit var mWifiManager: WifiManager
    private lateinit var mReceiver: BroadcastReceiver

    override fun getLayoutId(): Int {
        return R.layout.activity_engineer
    }

    override fun init() {
        mWifiManager = WifiManager(this)
        initWeb()
        mPaw = intent.getStringExtra(PASSWORD) ?: ""
        padID = intent.getStringExtra(NAME) ?: "ITL_PAD"
        AppExecutors.handler().postDelayed({
            showLog("正在关闭BLE服务")
            BleMaintainService.stopService(this)
            initApAndWeb(padID, mPaw)
        }, 1000)
        showInit(true, padID, mPaw)
        engnieerConfig = EngnieerConfig()
        registerReceiveWifi()
    }

    // 注册WIFI热点广播
    private fun registerReceiveWifi() {
        log("开始注册WIFI热点广播")
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                log("收到广播,action->$action")
                if ("android.net.wifi.WIFI_AP_STATE_CHANGED" == action) {
                    val state = intent.getIntExtra(android.net.wifi.WifiManager.EXTRA_WIFI_STATE, 0)
                    log("wifi status->$state")
                    when(state) {
                        13 -> startWeb()
                    }
                }
            }
        }
        val intentFilter = IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED")
        registerReceiver(mReceiver, intentFilter)
    }

    private fun initApAndWeb(name: String = padID, psw: String = mPaw) {
        AppExecutors.diskIO().execute {
            showLog("正在开启WIFI热点,热点名->$name")
            // 开启AP
            mWifiManager.closeWifiHotspot()
            Thread.sleep(5000)
            val wifiApEnabled = mWifiManager.setWifiApEnabled(true, name, psw)
            log("initApAndWeb 开启WIFI:${wifiApEnabled}, 名称:${name}, 密码:$psw")
        }
    }

    private fun initWeb() {
        mWebManager = WebCoreManager.instance
        log("初始化web ${mWebManager == null}")
        mWebManager.mListener = object : WebCoreManager.WebServerListener {
            override fun onException(exception: Exception) {
                mWebManager.stop()
                AppExecutors.diskIO().execute {
                    startWeb()
                }
            }

            override fun onStarted(ip: String) {
                showLog("WEB服务开启,ip地址为->$ip")
                showInit(false, padID, mPaw)
            }

            override fun onStopped() {
                showLog("WEB服务关闭")
            }
        }
    }

    private fun showInit(isInit: Boolean, name: String, psw: String) {
        AppExecutors.mainThread().execute {
        }
    }

    private fun startWeb() {
        launch {
            do {
                delay(500)
            } while (!NetUtils.isIpReady(WebCoreManager.hostIp))
            showLog("正在开启WEB服务")
            mWebManager.start()
        }
    }

    fun showLog(log: String) {
        log(log)
        EventBus.getDefault().post(EngineerLogEvent(log))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEngineerLogEvent(event: EngineerLogEvent) {
        var log = binding.tvLog.text.toString() + "\n"
        log += event.result
        binding.tvLog.text = log
        AppExecutors.handler().post {
            binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnEngineerWebEvent(event : EngineerWebEvent) {
        when (event.aipName) {
            "login" -> {
                showLog("有工程APP登录进来")
            }
            "submitAll" -> {
                // TODO
            }
        }
    }

    companion object {
        const val PASSWORD = "password"
        const val NAME = "NAME"
        const val IS_INIT = "IS_INIT"
        lateinit var engnieerConfig: EngnieerConfig

        fun startActivity(context: Context) : String {
            // 随机生成热点的名称和密码
            val psw = String.format("%08d", Random().nextInt(9999999))
            val name = "ITL_${String.format("%04d", Random().nextInt(9999))}"
            log("psw:$psw, name:$name") // psw:00031930, name:ITL_5043
            val intent = Intent(context, EngineerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(PASSWORD, psw)
            intent.putExtra(NAME, name)
            intent.putExtra(IS_INIT, false)
            context.startActivity(intent)
            return "$name,$psw"
        }
    }

    override fun onDestroy() {
        mWebManager.mListener = null
        mWebManager.stop()
        mWifiManager.closeWifiHotspot()
        showLog("正在恢复BLE服务")
        unregisterReceiver(mReceiver)
        cancel()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }
}
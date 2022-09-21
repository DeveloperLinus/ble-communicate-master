package com.ble.communicate.ui

import android.util.Log
import android.widget.ScrollView
import com.ble.commonlib.base.BaseBindingActivity
import com.ble.commonlib.base.log
import com.ble.commonlib.core.utils.AppExecutors
import com.ble.communicate.R
import com.ble.communicate.core.EngineerLogEvent
import com.ble.communicate.core.WebCoreManager
import com.ble.communicate.databinding.ActivityPostmanTestBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.util.*

class PostmanTestActivity : BaseBindingActivity<ActivityPostmanTestBinding>() {
    private lateinit var mWebManager: WebCoreManager // WEB服务
    override fun getLayoutId(): Int {
        return R.layout.activity_postman_test
    }

    override fun init() {
        initWeb()
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
                showLog("WEB服务开启,ip地址->$ip")
            }

            override fun onStopped() {
                showLog("WEB服务关闭")
            }
        }
        startWeb()
    }

    private fun startWeb() {
        mWebManager.start()
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

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        mWebManager.mListener = null
        mWebManager.stop()
        super.onDestroy()
    }

    private fun log(msg: String) {
        Log.d("PostmanTest", msg)
    }
}
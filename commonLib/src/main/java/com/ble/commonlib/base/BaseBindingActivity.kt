package com.ble.commonlib.base

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseBindingActivity<T : ViewDataBinding> : Activity() {
    private val FLAG_HOMEKEY_DISPATCHED = -0x80000000 //定义屏蔽参数
    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        window.setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED) //onCreate中实现

        hideBottomUIMenu()
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        init()
        initListener()
    }

    abstract fun getLayoutId(): Int
    abstract fun init()
    open fun initListener() {}

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideBottomUIMenu()
        }
    }

    protected open fun hideBottomUIMenu() {
        var uiFlags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN) // hide status bar
        uiFlags = if (Build.VERSION.SDK_INT >= 19) {
            uiFlags or 0x00001000 //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags or View.SYSTEM_UI_FLAG_LOW_PROFILE
        }
        window.decorView.systemUiVisibility = uiFlags
    }

    override fun onResume() {
        super.onResume()
    }
}
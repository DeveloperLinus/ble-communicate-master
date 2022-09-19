package com.ble.communicate.ui

import com.ble.commonlib.base.BaseBindingActivity
import com.ble.communicate.R
import com.ble.communicate.databinding.ActivityMainBinding
import com.ble.communicate.service.BleMaintainService

class MainActivity : BaseBindingActivity<ActivityMainBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onResume() {
        super.onResume()
        BleMaintainService.startService(this)
    }

    override fun init() {

    }

    override fun onPause() {
        super.onPause()
        BleMaintainService.stopService(this)
    }
}
package com.ble.communicate.core

import com.ble.commonlib.base.RapidSPRepository
import com.ble.commonlib.cache.utils.RapidSP
import com.ble.communicate.model.NetPadConfig

class EngnieerConfig {
    var padConfig = NetPadConfig()
    private val model = RapidSPRepository.provide()
    var token: String = ""

    init {
        padConfig.initConfig()
        token = model!!.getToken()
    }

    fun update() {
        padConfig.initConfig()
        token = model!!.getToken()
    }
}
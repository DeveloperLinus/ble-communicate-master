package com.ble.communicate.core

import com.ble.commonlib.base.RapidSPRepository
import com.ble.commonlib.cache.utils.RapidSP
import com.ble.communicate.model.NetPadConfig

class EngnieerConfig {
    var token: String = ""
    var padConfig = NetPadConfig()
    private val model = RapidSPRepository.provide()

    init {
        padConfig.initConfig()
        token = model!!.getToken()
    }
}
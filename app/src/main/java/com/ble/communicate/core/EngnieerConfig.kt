package com.ble.communicate.core

import com.ble.communicate.model.NetPadConfig

class EngnieerConfig {
    var token: String = ""
    var padConfig = NetPadConfig()

    init {
        padConfig.initConfig()
    }
}
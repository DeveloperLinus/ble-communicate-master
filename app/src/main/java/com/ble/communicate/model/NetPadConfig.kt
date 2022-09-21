package com.ble.communicate.model

import com.ble.roomlib.db.manager.PadConfigManager

data class NetPadConfig(
    var deviceUseEnvironment: String = "",
    var selectDeviceType: String = "",
    var projectName: String = "",
    var deviceNumber: String = "",
    var communicationIp: String = "",
    var authorizationPassword: String = ""
) {
    fun initConfig() {
        val config = PadConfigManager.getPadConfig()
        config?.let {
            deviceUseEnvironment = it.deviceUseEnvironment
            selectDeviceType = it.deviceType
            projectName = it.projectNumber
            deviceNumber = it.deviceNumber
            communicationIp = it.communicationIp
        }
    }
}
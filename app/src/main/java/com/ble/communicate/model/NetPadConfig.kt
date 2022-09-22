package com.ble.communicate.model

import com.ble.roomlib.db.manager.PadConfigManager

data class NetPadConfig(
    var deviceUseEnvironment: String = "",
    var selectDeviceType: String = "",
    var projectName: String = "",
    var  plateNumber: String = "",
    var equipmentNumber: String = "",
    var communicationProtocol: String = "",
    var bindIp: String = "",
    var delayCallLift: String = "",
    var installFloorName: String = "",
    var readHeadNumber: String = "",
    var  installPosition: String = "",
    var voiceReader: String = "",
    var communicationIp: String = "",
    var authorizationPassword: String = ""
) {
    fun initConfig() {
        val config = PadConfigManager.getPadConfig()
        config?.let {
            deviceUseEnvironment = it.deviceUseEnvironment
            selectDeviceType = it.deviceType
            projectName = it.projectNumber
            plateNumber = it.plateNumber
            equipmentNumber = it.deviceNumber
            communicationProtocol = it.communicationProtocol
            bindIp = it.bindIp
            delayCallLift = it.delayCallLift
            installFloorName = it.installFloorName
            readHeadNumber = it.readHeadNumber
            installPosition = it.installPosition
            voiceReader = it.voiceReader
            communicationIp = it.communicationIp
        }
    }
}
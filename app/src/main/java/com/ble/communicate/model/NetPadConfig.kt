package com.ble.communicate.model

import android.text.TextUtils
import com.ble.roomlib.db.manager.PadConfigManager

data class NetPadConfig(
    var deviceUseEnvironment: String = "1",
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
    var cardMode: String = "0",
    var voiceReader: String = "",
    var communicationIp: String = "",
    var floorSelection: String = "",
    var authorizationPassword: String = ""
) {
    var projectNumberBuffer: String = ""
    private set
    get() = if (field.isEmpty()) {
        projectName
    } else {
        field
    }
    fun initConfig() {
        val config = PadConfigManager.getPadConfig()
        config?.let {
            deviceUseEnvironment = it.deviceUseEnvironment
            if (TextUtils.isEmpty(deviceUseEnvironment)) {
                deviceUseEnvironment = "1"
            }
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
//            communicationIp = it.communicationIp
            communicationIp = ""
            floorSelection = it.floorSelection
        }
    }
}
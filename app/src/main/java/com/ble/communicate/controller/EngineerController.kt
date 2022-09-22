package com.ble.communicate.controller

import android.text.TextUtils
import com.ble.commonlib.base.RapidSPRepository
import com.ble.commonlib.base.log
import com.ble.communicate.core.EngineerLogEvent
import com.ble.communicate.core.EngineerWebEvent
import com.ble.communicate.model.NetPadConfig
import com.ble.communicate.ui.EngineerActivity
import com.ble.roomlib.db.entity.PadConfigEntity
import com.ble.roomlib.db.manager.PadConfigManager
import com.yanzhenjie.andserver.annotation.*
import com.yanzhenjie.andserver.util.MediaType
import org.greenrobot.eventbus.EventBus

@RestController
@RequestMapping(path = ["/engineer"])
class EngineerController {
    private val model = RapidSPRepository.provide()

    @PostMapping(path = ["/login"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun login(
        @RequestParam(name = "userName") userName: String,
        @RequestParam(name = "password") password: String
    ): String {
        var token = model!!.getToken()
        if (TextUtils.isEmpty(token)) {
            token = "9S33yyymMtiTc4f1NZfC0Q=="
            model.setToken(token)
            EngineerActivity.engnieerConfig.update()
        }
        val result =
            "api:login,msg = 工程账号登录成功，账号名->->${userName}, 密码->${password}, 令牌->$token" // 登录的账号名:->HQHYMJ001, 密码->itlong@123
        showLog(result)
        postWebMsg("login")
        return token
    }

    @Addition(stringType = ["login"], booleanType = [true])
    @PostMapping(path = ["/getPadConfig"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getPadConfig(@RequestParam(name = "token") token: String): NetPadConfig {
        val token2 = EngineerActivity.engnieerConfig.token
        val msg = "接收到的token1->$token，设备的token2 ->$token2"
        val config = EngineerActivity.engnieerConfig.padConfig
        val authPwd = "test7777" // 授权密码，文件中读取
        config.authorizationPassword = authPwd
        showLog(msg)
        return config
    }

    @Addition(stringType = ["login"], booleanType = [true])
    @PostMapping(path = ["/setPadConfig"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun setPadConfig(
        @RequestParam(name = "token") token: String,
        @RequestParam(name = "selectDeviceType") selectDeviceType: String,
        @RequestParam(name = "projectNumber") projectNumber: String,
        @RequestParam(name = "plateNumber") plateNumber: String,
        @RequestParam(name = "equipmentNumber") equipmentNumber: String,
        @RequestParam(name = "communicationProtocol") communicationProtocol: String,
        @RequestParam(name = "bindIp") bindIp: String,
        @RequestParam(name ="delayCallLift") delayCallLift:String,
        @RequestParam(name="installFloorName") installFloorName: String,
        @RequestParam(name = "deviceUseEnvironment") deviceUseEnvironment: String,
        @RequestParam(name = "readHeadNumber") readHeadNumber: String,
        @RequestParam(name = "communicationIp") communicationIp: String,
        @RequestParam(name = "authorizationPassword") authorizationPassword: String,
        @RequestParam(name = "installPosition") installPosition : String,
        @RequestParam(name = "voiceReader") voiceReader: String
    ): String {
        showLog("token:$token,selectDeviceType:$selectDeviceType,projectNumber:$projectNumber,plateNumber:$plateNumber,equipmentNumber:$equipmentNumber," +
                "communicationProtocol:$communicationProtocol, bindIp:$bindIp, delayCallLift:$delayCallLift, installFloorName:$installFloorName, deviceUseEnvironment:$deviceUseEnvironment," +
                "readHeadNumber:$readHeadNumber, communicationIp:$communicationIp,$authorizationPassword, installPosition:$installPosition, voiceReader:$voiceReader")
        val padConfig = PadConfigManager.getPadConfig()
        val padConfigEntity = PadConfigEntity().apply {
            this.token = token
            this.deviceType = selectDeviceType
            this.projectNumber = projectNumber
            this.plateNumber = plateNumber
            this.deviceNumber = equipmentNumber
            this.communicationProtocol = communicationProtocol
            this.bindIp = bindIp
            this.delayCallLift = delayCallLift
            this.installFloorName = installFloorName
            this.readHeadNumber = readHeadNumber
            this.installPosition = installPosition
            this.voiceReader = voiceReader
            this.communicationIp = communicationIp
            this.token = token
        }
        if (padConfig == null) {
            PadConfigManager.insert(padConfigEntity)
        } else {
            PadConfigManager.update(padConfigEntity)
        }
        EngineerActivity.engnieerConfig.update()
        return "OK"
    }

    private fun showLog(msg: String) {
        log(msg)
        EventBus.getDefault().post(EngineerLogEvent(msg))
    }

    private fun postWebMsg(apiName: String) {
        EventBus.getDefault().post(EngineerWebEvent(apiName))
    }
}
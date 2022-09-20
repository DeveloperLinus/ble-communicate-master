package com.ble.communicate.controller

import com.ble.commonlib.base.log
import com.ble.commonlib.cache.utils.EngineerDataSourceImpl
import com.ble.communicate.core.EngineerLogEvent
import com.yanzhenjie.andserver.annotation.*
import com.yanzhenjie.andserver.util.MediaType
import org.greenrobot.eventbus.EventBus

@RestController
@RequestMapping(path=["/engineer"])
class EngineerController {
    @PostMapping(path = ["/login"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun login(@RequestParam(name = "userName") userName: String,
              @RequestParam(name = "password") password: String): String {
        val result = "api:login,msg = 工程账号登录成功，账号名->->${userName}, 密码->${password}" // 登录的账号名:->HQHYMJ001, 密码->itlong@123
        val token = "9S33yyymMtiTc4f1NZfC0Q=="
        showLog(result)
        return token
    }

    @Addition(stringType = ["login"], booleanType = [true])
    @PostMapping(path = ["/getPadConfig"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getPadConfig(@RequestParam(name = "token") token: String) {
        log("token->$token")
    }

    fun showLog(msg: String) {
        log(msg)
        EventBus.getDefault().post(EngineerLogEvent(msg))
    }
}
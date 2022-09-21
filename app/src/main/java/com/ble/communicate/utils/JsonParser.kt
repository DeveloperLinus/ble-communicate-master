package com.ble.communicate.utils

import com.ble.commonlib.base.log
import com.ble.commonlib.core.utils.JsonUtil
import com.ble.communicate.web.model.RespData

object JsonParser {
    fun successfulJson(data: Object): String {
        val respData = RespData()
        respData.isSuccess = true
        respData.errorCode = 200
        respData.data = data
        log("successfulJson data->${respData.data}")
        val result = JsonUtil.toJsonString2(respData)
//        val result = JsonUtil.toJsonString(respData)!! // "9S33yyymMtiTc4f1NZfC0Q\u003d\u003d"
        log("successfulJson result->$result")
        return result!!
    }

    fun failedJson(code: Int, message: String): String {
        val respData = RespData()
        respData.isSuccess = false
        respData.errorCode = code
        respData.errorMsg = message
//        return JsonUtil.toJsonString(respData)!!
        return JsonUtil.toJsonString2(respData)!!
    }
}
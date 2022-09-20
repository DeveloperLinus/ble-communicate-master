package com.ble.communicate.utils

import com.ble.commonlib.core.utils.JsonUtil
import com.ble.communicate.web.model.RespData

object JsonParser {
    fun successfulJson(data: Object): String {
        val respData = RespData()
        respData.isSuccess = true
        respData.errorCode = 200
        respData.data = data
        return JsonUtil.toJsonString(respData)!!
    }

    fun failedJson(code: Int, message: String): String {
        val respData = RespData()
        respData.isSuccess = false
        respData.errorCode = code
        respData.errorMsg = message
        return JsonUtil.toJsonString(respData)!!
    }
}
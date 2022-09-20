package com.ble.communicate.web

import com.ble.commonlib.base.log
import com.ble.commonlib.core.utils.JsonUtil
import com.ble.communicate.utils.JsonParser
import com.yanzhenjie.andserver.annotation.Resolver
import com.yanzhenjie.andserver.error.BasicException
import com.yanzhenjie.andserver.framework.ExceptionResolver
import com.yanzhenjie.andserver.framework.body.JsonBody
import com.yanzhenjie.andserver.http.HttpRequest
import com.yanzhenjie.andserver.http.HttpResponse
import com.yanzhenjie.andserver.util.StatusCode

@Resolver
class AppExceptionResolver : ExceptionResolver{
    override fun onResolve(request: HttpRequest, response: HttpResponse, e: Throwable) {
        log("AppExceptionResolver onResolve")
        if (e is BasicException) {
            response.status = e.statusCode
        } else {
            response.status = StatusCode.SC_INTERNAL_SERVER_ERROR
        }
        val body = JsonParser.failedJson(response.status, e.message!!)
        response.setBody(JsonBody(body))
    }
}
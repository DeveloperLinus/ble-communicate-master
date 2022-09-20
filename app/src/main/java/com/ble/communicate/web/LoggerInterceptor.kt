package com.ble.communicate.web

import com.ble.commonlib.base.log
import com.ble.commonlib.core.utils.JsonUtil
import com.yanzhenjie.andserver.annotation.Interceptor
import com.yanzhenjie.andserver.framework.HandlerInterceptor
import com.yanzhenjie.andserver.framework.handler.RequestHandler
import com.yanzhenjie.andserver.http.HttpRequest
import com.yanzhenjie.andserver.http.HttpResponse

// 使用AndServer加上该注解可以生成拦截器
@Interceptor
class LoggerInterceptor : HandlerInterceptor {
    override fun onIntercept(request: HttpRequest, response: HttpResponse, handler: RequestHandler): Boolean {
        val path = request.path
        val method = request.method
        val valueMap = request.getParameter()
        log("path->$path")
        log("method->$method")
        log("valueMap->${JsonUtil.toJsonString(valueMap)}")
        return false
    }
}
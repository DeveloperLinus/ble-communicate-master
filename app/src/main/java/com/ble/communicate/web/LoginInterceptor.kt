package com.ble.communicate.web

import android.text.TextUtils
import com.ble.commonlib.base.RapidSPRepository
import com.ble.commonlib.base.log
import com.yanzhenjie.andserver.annotation.Interceptor
import com.yanzhenjie.andserver.framework.HandlerInterceptor
import com.yanzhenjie.andserver.framework.handler.MethodHandler
import com.yanzhenjie.andserver.framework.handler.RequestHandler
import com.yanzhenjie.andserver.http.HttpRequest
import com.yanzhenjie.andserver.http.HttpResponse
import com.yanzhenjie.andserver.mapping.Addition
import java.lang.reflect.Array


// AndServer使用该注解可以生成拦截器
@Interceptor
class LoginInterceptor : HandlerInterceptor {
    private val model = RapidSPRepository.provide()
    override fun onIntercept(
        request: HttpRequest,
        response: HttpResponse,
        handler: RequestHandler
    ): Boolean {
        log("start onIntercept, handler->$handler, boolean->${handler is MethodHandler}")
        if (handler is MethodHandler) {
            val methodHandler = handler
            val addition = methodHandler.addition
            if (isNeedLogin(addition!!)) {
                if (!isLogin()) {
                    log("ErrorOnLogin")
                    throw BaseWebException(EnumWebError.ErrorOnLogin)
                }
                if (!checkToken(request)) {
                    log("ErrorLoginOccupy")
                    throw BaseWebException(EnumWebError.ErrorLoginOccupy)
                }
            }
        }
        log("end onIntercept")
        return false
    }

    private fun isNeedLogin(addition: Addition?): Boolean {
        try {
            log("start  isNeedLogin")
            if (addition == null) return false
            log("addition is not null")
            var stringTypes = addition.stringType
            log("stringTypes->${stringTypes}")
            if (isEmpty(stringTypes)) return false
            var booleanTypes = addition.booleanType
            log("booleanTypes->$booleanTypes")
            if (isEmpty(booleanTypes)) return false
            return stringTypes[0].equals("login", ignoreCase = true) && booleanTypes[0]
        } catch (exception: Exception) {
            log("isNeedLogin 报错->${exception.message}")
            return false
        }
    }

    private fun isLogin() : Boolean {
        log("start isLogin")
        val token = model?.getToken()
        log("isLogin是否已经登录,token->$token")
        if (TextUtils.isEmpty(token)) {
            return false
        } else {
            return true
        }
    }

    private fun checkToken(request: HttpRequest): Boolean {
        log("start checkToken")
        val parameter = request.getParameter()
        val tokens = parameter.get("token")
        val localToken = model!!.getToken()
        tokens?.let {
            for (token in it) {
                if (localToken == token) {
                    return true
                }
            }
        }
        return false
    }

    private fun isEmpty(array: Any?): Boolean {
        log("start isEmpty")
        if (array == null) {
            return true
        }
        return Array.getLength(array) == 0
    }
}
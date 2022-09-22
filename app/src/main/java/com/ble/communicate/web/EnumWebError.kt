package com.ble.communicate.web

enum class EnumWebError(var errorCode: Int, var errorMsg: String) {
    ErrorOnLogin(301,"请登录工程APP"),

    ErrorLoginOccupy(302,"系统已经被占用")
}
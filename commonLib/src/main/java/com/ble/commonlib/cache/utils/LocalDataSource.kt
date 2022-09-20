package com.ble.commonlib.cache.utils

interface LocalDataSource {
    fun setToken(token: String)
    fun getToken(): String
}
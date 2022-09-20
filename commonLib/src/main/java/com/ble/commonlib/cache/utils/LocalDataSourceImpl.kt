package com.ble.commonlib.cache.utils

import com.ble.commonlib.base.log

class LocalDataSourceImpl : LocalDataSource {
    private val TAG = "LocalDataSource"

    companion object {
        @Volatile
        private var INSTANCE: LocalDataSourceImpl? = null

        @JvmStatic
        fun getInstance(): LocalDataSourceImpl? {
            if (INSTANCE == null) {
                synchronized(LocalDataSourceImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = LocalDataSourceImpl()
                    }
                }
            }
            return INSTANCE
        }
    }

    override fun setToken(token: String) {
        log("setToken token->$token")
        RapidSP[TAG]?.edit()?.putString("token", token)?.apply()
    }

    override fun getToken(): String {
        log("getToken")
        return RapidSP[TAG]?.getString("token", "")!!
    }
}
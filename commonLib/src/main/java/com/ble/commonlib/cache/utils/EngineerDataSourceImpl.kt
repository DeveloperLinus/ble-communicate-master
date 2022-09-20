package com.ble.commonlib.cache.utils

class EngineerDataSourceImpl : LocalDataSource{
    companion object {
        private const val TAG = "EngineerDataSource"
        private lateinit var INSTANCE: EngineerDataSourceImpl
        fun getInstance(): EngineerDataSourceImpl {
            if (INSTANCE == null) {
                synchronized(EngineerDataSourceImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = EngineerDataSourceImpl()
                    }
                }
            }
            return INSTANCE
        }
    }
    override fun setToken(token: String) {
        RapidSP.get(TAG)?.edit()?.putString("token", token)
    }

    override fun getToken(): String {
        return RapidSP.get(TAG)?.getString("token", "")!!
    }
}
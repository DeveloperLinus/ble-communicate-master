package com.ble.commonlib.base

import com.ble.commonlib.cache.utils.LocalDataSource

class BaseRepository(var mLocalDataSource: LocalDataSource) : LocalDataSource{
    companion object {
        @Volatile
        private var INSTANCE: BaseRepository? = null

        @JvmStatic
        fun getInstance(localDataSource: LocalDataSource): BaseRepository? {
            if (INSTANCE == null) {
                synchronized(BaseRepository::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = BaseRepository(localDataSource)
                    }
                }
            }
            return INSTANCE
        }
    }
    override fun setToken(token: String) {
        mLocalDataSource.setToken(token)
    }

    override fun getToken(): String {
        return mLocalDataSource.getToken()
    }
}
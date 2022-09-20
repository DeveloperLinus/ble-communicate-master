package com.ble.commonlib.base

import android.content.Context
import com.ble.commonlib.cache.utils.LocalDataSourceImpl
import com.ble.commonlib.cache.utils.RapidSP

class Repository {
    companion object {
        fun provide() : BaseRepository? {
            val localDataSource = LocalDataSourceImpl.getInstance()
            return BaseRepository.getInstance(localDataSource!!)
        }

        fun init(context: Context) {
            log("初始化Rsp存储")
            RapidSP.init(context)
        }
    }
}
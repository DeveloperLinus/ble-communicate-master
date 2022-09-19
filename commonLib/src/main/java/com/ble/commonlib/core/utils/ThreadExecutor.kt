package com.ble.commonlib.core.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object ThreadExecutor {
    val WORK_IO: Executor by lazy { Executors.newFixedThreadPool(10) }
    val MAIN: Executor by lazy { MainThreadExecutor() }
    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private class MainThreadExecutor : Executor {
        private val mainThreadExecutor = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadExecutor.post(command)
        }
    }
}
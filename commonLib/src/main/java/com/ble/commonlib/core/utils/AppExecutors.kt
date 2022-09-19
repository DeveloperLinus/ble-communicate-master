package com.ble.commonlib.core.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.*

object AppExecutors {
    private val mDiskIO by lazy {
        Executors.newSingleThreadExecutor(MyThreadFactory("single"))
    }

    private val mNetworkIO by lazy {
        Executors.newFixedThreadPool(3, MyThreadFactory("fixed"))
    }

    private val mMainThread by lazy {
        MainThreadExecutor()
    }

    private val mCacheThreadPool by lazy {
        Executors.newCachedThreadPool(MyThreadFactory("cache"))
    }

    private val schedule by lazy {
        ScheduledThreadPoolExecutor(
            5,
            MyThreadFactory("sc"),
            ThreadPoolExecutor.AbortPolicy()
        )
    }

    private val mHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    internal class MyThreadFactory(private val name: String) :
        ThreadFactory {
        private var count = 0
        override fun newThread(r: Runnable): Thread {
            count++
            return Thread(r, "$name-$count-Thread")
        }
    }

    fun cachePool(): Executor {
        return mCacheThreadPool
    }

    fun diskIO(): Executor {
        return mDiskIO
    }

    fun schedule(): ScheduledThreadPoolExecutor {
        return schedule
    }

    fun networkIO(): Executor {
        return mNetworkIO
    }

    fun mainThread(): Executor {
        return mMainThread
    }

    fun handler(): Handler {
        return mHandler
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler =
            Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}
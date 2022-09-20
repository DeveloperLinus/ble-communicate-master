package com.ble.commonlib.cache.utils

import android.content.Context
import android.util.Log
import com.ble.commonlib.base.log
import java.io.*
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class ReadWriteManager(context: Context, name: String) {
    private val filePath: String
    private val lockFilePath: String
    fun write(obj: Any?) {
        var oos: ObjectOutputStream? = null
        var fos: FileOutputStream? = null
        var lock: Lock? = null
        try {
            prepare()
            lock = Lock(lockFilePath).lock()
            log("start write file: $filePath")
            fos = FileOutputStream(filePath)
            oos = ObjectOutputStream(BufferedOutputStream(fos))
            oos.writeObject(obj)
            oos.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            log( "finish write file: $filePath")
            IoUtil.closeSilently(oos)
            IoUtil.closeSilently(fos)
            lock?.release()
        }
    }

    fun read(): Any? {
        if (!IoUtil.isFileExist(filePath)) {
            return null
        }
        var ois: ObjectInputStream? = null
        var fis: FileInputStream? = null
        var lock: Lock? = null
        return try {
            lock = Lock(lockFilePath).lock()
            fis = FileInputStream(filePath)
            ois = ObjectInputStream(BufferedInputStream(fis))
            ois.readObject()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            IoUtil.closeSilently(ois)
            IoUtil.closeSilently(fis)
            lock?.release()
        }
    }

    private fun prepare() {
        val file = File(filePath)
        val parent = file.parentFile
        if (!parent.exists()) {
            parent.mkdirs()
        }
    }

    private class Lock(private val lockFilePath: String) {
        private var fos: FileOutputStream? = null
        private var channel: FileChannel? = null
        private var fileLock: FileLock? = null
        private val threadLock: ReentrantLock?
        @Throws(IOException::class)
        fun lock(): Lock {
            threadLock!!.lock()
            fos = FileOutputStream(lockFilePath)
            channel = fos!!.channel
            fileLock = channel!!.lock()
            return this
        }

        fun release() {
            if (fileLock != null) {
                try {
                    fileLock!!.release()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            IoUtil.closeSilently(channel)
            IoUtil.closeSilently(fos)
            threadLock!!.unlock()
        }

        companion object {
            private val THREAD_LOCK_MAP: MutableMap<String, ReentrantLock?> = HashMap()
            private fun getLock(key: String): ReentrantLock? {
                synchronized(THREAD_LOCK_MAP) {
                    if (!THREAD_LOCK_MAP.containsKey(key)) {
                        THREAD_LOCK_MAP[key] = ReentrantLock()
                    }
                    return THREAD_LOCK_MAP[key]
                }
            }
        }

        init {
            threadLock = getLock(lockFilePath)
            val file = File(lockFilePath)
            if (!file.exists()) {
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_DIR_PATH = "rapidSP"
        fun getFilePath(context: Context, name: String): String {
            return (context.filesDir.absolutePath
                    + File.separator
                    + DEFAULT_DIR_PATH
                    + File.separator
                    + name)
        }
    }

    init {
        filePath = getFilePath(context, name)
        lockFilePath = getFilePath(context, "$name.lock")
        prepare()
    }
}
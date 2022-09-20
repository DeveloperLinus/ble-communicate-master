package com.ble.commonlib.cache.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.FileObserver
import android.util.Log
import android.util.LruCache
import com.ble.commonlib.base.log
import java.io.File
import java.io.Serializable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

class RapidSP private constructor(private val name: String) : EnhancedSP {
    private val keyValueMap: MutableMap<String, Any?>
    private val editor: RspEditor = RspEditor()
    private val needSync = AtomicBoolean(false)
    private val syncing = AtomicBoolean(false)

    //这个锁主要是为了锁住拷贝数据的过程，当进行数据拷贝的时候，不允许任何写入操作
    private val copyLock: ReadWriteLock = ReentrantReadWriteLock()
    private val observer: DataChangeObserver
    override fun getAll(): Map<String, *> {
        return keyValueMap
    }

    override fun getString(s: String?, s1: String?): String? {
        return if (keyValueMap.containsKey(s)) {
            keyValueMap[s] as String?
        } else s1
    }

    override fun getSerializable(key: String, defValue: Serializable): Serializable {
        return if (keyValueMap.containsKey(key)) {
            keyValueMap[key] as Serializable
        } else defValue
    }

    override fun getStringSet(s: String?, set: Set<String>?): Set<String>? {
        return if (keyValueMap.containsKey(s)) {
            keyValueMap[s] as Set<String>?
        } else set
    }

    override fun getInt(s: String?, i: Int): Int {
        return if (keyValueMap.containsKey(s)) {
            keyValueMap[s] as Int
        } else i
    }

    override fun getLong(s: String?, l: Long): Long {
        return if (keyValueMap.containsKey(s)) {
            keyValueMap[s] as Long
        } else l
    }

    override fun getFloat(s: String?, v: Float): Float {
        return if (keyValueMap.containsKey(s)) {
            keyValueMap[s] as Float
        } else v
    }

    override fun getBoolean(s: String?, b: Boolean): Boolean {
        return if (keyValueMap.containsKey(s)) {
            keyValueMap[s] as Boolean
        } else b
    }

    override fun contains(s: String?): Boolean {
        return keyValueMap.containsKey(s)
    }

    override fun edit(): EnhancedEditor {
        return editor
    }

    override fun registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener) {}
    override fun unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener) {}
    private fun reload() {
        log("reload data")
        val loadedData: Any? = ReadWriteManager(sContext!!, name).read()
        keyValueMap.clear()
        if (loadedData != null) {
            keyValueMap.putAll((loadedData as Map<out String, *>))
        }
    }

    private fun sizeOf(): Int {
        val file: File = File(ReadWriteManager.getFilePath(sContext!!, name))
        return if (!file.exists()) {
            0
        } else (file.length() / 1024).toInt()
    }

    private inner class RspEditor : EnhancedEditor {
        override fun putSerializable(key: String, value: Serializable?): EnhancedEditor {
            put(key, value)
            return this
        }

        override fun putString(s: String, s1: String?): EnhancedEditor {
            put(s, s1)
            return this
        }

        override fun putStringSet(s: String, set: Set<String?>?): EnhancedEditor {
            put(s, set)
            return this
        }

        override fun putInt(s: String, i: Int): EnhancedEditor {
            put(s, i)
            return this
        }

        override fun putLong(s: String, l: Long): EnhancedEditor {
            put(s, l)
            return this
        }

        override fun putFloat(s: String, v: Float): EnhancedEditor {
            put(s, v)
            return this
        }

        override fun putBoolean(s: String, b: Boolean): EnhancedEditor {
            put(s, b)
            return this
        }

        private fun put(s: String, obj: Any?) {
            copyLock.readLock().lock()
            keyValueMap[s] = obj
            copyLock.readLock().unlock()
        }

        override fun remove(s: String?): SharedPreferences.Editor {
            copyLock.readLock().lock()
            keyValueMap.remove(s)
            copyLock.readLock().unlock()
            return this
        }

        override fun clear(): EnhancedEditor {
            copyLock.readLock().lock()
            keyValueMap.clear()
            copyLock.readLock().unlock()
            return this
        }

        override fun commit(): Boolean {
            sync()
            return true
        }

        override fun apply() {
            sync()
        }

        private fun sync() {
            needSync.compareAndSet(false, true)
            postSyncTask()
        }

        @Synchronized
        private fun postSyncTask() {
            if (syncing.get()) {
                return
            }
            SYNC_EXECUTOR.execute(SyncTask())
        }

        private inner class SyncTask : Runnable {
            override fun run() {
                if (!needSync.get()) {
                    return
                }
                //先把syncing标记置为true
                syncing.compareAndSet(false, true)
                //copy map，copy的过程中不允许写入
                copyLock.writeLock().lock()
                val storeMap: Map<String, Any?> = HashMap(keyValueMap)
                copyLock.writeLock().unlock()
                //把needSync置为false，如果在此之后有数据写入，则需要重新同步
                needSync.compareAndSet(true, false)
                observer.stopWatching()
                val manager = ReadWriteManager(sContext!!, name)
                manager.write(storeMap)
                //解除同步过程
                syncing.compareAndSet(true, false)
                log("write to file complete")
                //如果数据被更改，则需要重新同步
                if (needSync.get()) {
                    log( "need to sync again")
                    postSyncTask()
                } else {
                    log("do not need to sync, start watching")
                    observer.startWatching()
                }
            }
        }
    }

    private inner class ReloadTask : Runnable {
        override fun run() {
            reload()
        }
    }

    private inner class DataChangeObserver : FileObserver {
        constructor(path: String?) : super(path, MODIFY or CLOSE_WRITE or DELETE)
        override fun onEvent(event: Int, path: String?) {
            log( "DataChangeObserver: $event")
            when (event) {
                CLOSE_WRITE -> onCloseWrite(path)
                DELETE -> onDelete(path)
            }
        }

        fun onCloseWrite(path: String?) {
            if (syncing.get()) {
                //如果正在同步，则取消reload
                return
            }
            SYNC_EXECUTOR.execute(ReloadTask())
        }

        fun onDelete(path: String?) {
            keyValueMap.clear()
        }
    }

    private class RspCache @JvmOverloads constructor(maxSize: Int = DEFAULT_MAX_SIZE) : LruCache<String, RapidSP?>(maxSize) {
        override fun sizeOf(key: String, value: RapidSP?): Int {
            var size = 0
            if (value != null) {
                size = value.sizeOf()
            }
            log( "RspCache sizeOf $key is: $size")
            return size
        }

        override fun create(key: String): RapidSP? {
            return RapidSP(key)
        }

        override fun entryRemoved(evicted: Boolean, key: String, oldValue: RapidSP?, newValue: RapidSP?) {
            log( "RspCache entryRemoved: $key")
        }

        companion object {
            private val DEFAULT_MAX_SIZE = (Runtime.getRuntime().maxMemory() / 1024 / 16).toInt()
        }
    }

    companion object {
        private val RSP_CACHE = RspCache()
        private val SYNC_EXECUTOR = Executors.newFixedThreadPool(4)
        private var sContext: Context? = null
        fun init(context: Context?) {
            if (context == null) {
                return
            }
            sContext = context.applicationContext
        }

        fun setMaxSize(maxSize: Int) {
            RSP_CACHE.resize(maxSize)
        }

        operator fun get(name: String?): RapidSP? {
            if (name == null || name.length == 0) {
                return null
            }
            synchronized(RapidSP::class.java) { return RSP_CACHE[name] }
        }
    }

    init {
        keyValueMap = ConcurrentHashMap()
        reload()
        observer = DataChangeObserver(ReadWriteManager.getFilePath(sContext!!, name))
        observer.startWatching()
    }
}
package com.ble.commonlib.core.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser

object JsonUtil {
    private const val TAG = "JsonUtil"

    /**
     * 转成json
     */
    fun toJsonString(obj: Any?): String? {
        if (obj is String) {
            return obj
        }
        try {
            return Gson().toJson(obj)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "toJsonString catch: \n${e.message}")
        }
        return null
    }

    /**
     * 转成JsonBean
     */
    fun <T> toJsonBean(json: String, cls: Class<T>?): T? {
        var t: T? = null
        try {
            /*Log.e(TAG, "=== : \n$json")*/
            t = Gson().fromJson(json, cls)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "toJsonBean catch: \n$json")
        }
        return t
    }

    @JvmStatic
    fun getValue(json: String, key: String): String {
        var eventType: String = ""
        try {
            eventType = JsonParser.parseString(json).asJsonObject[key].asString
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e(TAG, "getValue: json=$json")
        }
        return eventType
    }
}
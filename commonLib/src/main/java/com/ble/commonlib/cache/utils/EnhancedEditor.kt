package com.ble.commonlib.cache.utils

import android.content.SharedPreferences.Editor
import java.io.Serializable

interface EnhancedEditor : Editor {
    fun putSerializable(key: String, value: Serializable?): EnhancedEditor?
}
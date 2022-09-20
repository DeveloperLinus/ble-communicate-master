package com.ble.commonlib.cache.utils

import android.content.SharedPreferences
import androidx.annotation.Nullable
import java.io.Serializable

interface EnhancedSP : SharedPreferences {
    fun getSerializable(key: String, @Nullable defValue: Serializable) : Serializable
}
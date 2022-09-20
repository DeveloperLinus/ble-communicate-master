package com.ble.commonlib.cache.utils

import java.io.Closeable
import java.io.File
import java.io.IOException

object IoUtil {
    fun closeSilently(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (var2: IOException) {
            }
        }
    }

    fun isFileExist(path: String?): Boolean {
        val file = File(path)
        return file.exists()
    }
}

package com.ble.communicate.web

import android.content.Context
import com.ble.commonlib.base.log
import com.yanzhenjie.andserver.annotation.Config
import com.yanzhenjie.andserver.framework.config.Multipart
import com.yanzhenjie.andserver.framework.config.WebConfig
import java.io.File

@Config
class AppConfig : WebConfig {
    override fun onConfig(context: Context, delegate: WebConfig.Delegate) {
        log("AppConfig onConfig")
        delegate.setMultipart(
            Multipart.newBuilder()
                .allFileMaxSize(1024 * 1024 * 20)
                .fileMaxSize(1024 * 1024 * 5)
                .maxInMemorySize(1024 * 10)
                .uploadTempDir(File(context.cacheDir, "_server_upload_cache_"))
                .build())
    }
}
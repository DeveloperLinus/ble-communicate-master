package com.ble.communicate.web

import com.ble.commonlib.base.log
import com.ble.commonlib.core.utils.JsonUtil
import com.ble.communicate.utils.JsonParser
import com.yanzhenjie.andserver.annotation.Converter
import com.yanzhenjie.andserver.framework.MessageConverter
import com.yanzhenjie.andserver.framework.body.JsonBody
import com.yanzhenjie.andserver.http.ResponseBody
import com.yanzhenjie.andserver.util.IOUtils
import com.yanzhenjie.andserver.util.MediaType
import java.io.InputStream
import java.lang.reflect.Type

@Converter
class AppMessageConverter : MessageConverter {
    override fun convert(output: Any, mediaType: MediaType?): ResponseBody {
        log("AppMessageConverter start convert1")
        val body = JsonBody(JsonParser.successfulJson(output as Object))
        log("AppMessageConverter end convert1")
        return body
    }

    override fun <T : Any?> convert(stream: InputStream, mediaType: MediaType?, type: Type?): T? {
        log("AppMessageConverter start convert2")
        val charset = mediaType?.charset
        if (charset == null) {
            return JsonUtil.parseJson(IOUtils.toString(stream), type)
        }
        return JsonUtil.parseJson(IOUtils.toString(stream, charset), type)
    }
}
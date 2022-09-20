package com.ble.communicate.web

import com.yanzhenjie.andserver.error.BasicException

class BaseWebException(error: EnumWebError) : BasicException(error.errorCode, error.errorMsg) {
}
package com.ble.communicate.web.model

import android.os.Parcel
import android.os.Parcelable
import com.alibaba.fastjson.annotation.JSONField

class RespData() : Parcelable {
    @JSONField(name = "isSuccess")
    var isSuccess: Boolean? = null

    @JSONField(name = "msgCode")
    var errorCode: Int? = null

    @JSONField(name = "msg")
    var errorMsg: String? = null

    @JSONField(name = "data")
    var data: Object? = null

    constructor(parcel: Parcel) : this() {
        val zeroByte: Byte = 0x00
        isSuccess = parcel.readByte() != zeroByte
        errorCode = parcel.readInt()
        errorMsg = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByte((if (isSuccess!!) 1 else 0).toByte())
        dest.writeInt(errorCode!!)
        dest.writeString(errorMsg)
    }

    companion object CREATOR : Parcelable.Creator<RespData> {
        override fun createFromParcel(parcel: Parcel): RespData {
            return RespData(parcel)
        }

        override fun newArray(size: Int): Array<RespData?> {
            return arrayOfNulls<RespData>(size)
        }
    }
}
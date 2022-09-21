package com.ble.roomlib.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "padConfig")
data class PadConfigEntity(
    @ColumnInfo(name = "device_use_environment")
    var deviceUseEnvironment: String = "",
    @ColumnInfo(name = "select_device_type")
    var deviceType: String = "",
    @ColumnInfo(name = "project_number")
    var projectNumber: String = "",
    @ColumnInfo(name = "device_number")
    var deviceNumber:String ="",
    @ColumnInfo(name = "communication_ip")
    var communicationIp: String= "",
    var token: String =""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
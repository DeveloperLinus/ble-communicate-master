package com.ble.roomlib.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "padConfig")
data class PadConfigEntity(
    @ColumnInfo(name = "device_use_environment")
    var deviceUseEnvironment: String = "", //使用环境
    @ColumnInfo(name = "select_device_type")
    var deviceType: String = "", // 设备类型
    @ColumnInfo(name = "project_number")
    var projectNumber: String = "", // 项目编号
    @ColumnInfo(name = "plate_number")
    var plateNumber:String = "", // 平板机号
    @ColumnInfo(name = "device_number")
    var deviceNumber:String ="", // 设备机号
    @ColumnInfo(name = "communication_protocol")
    var communicationProtocol:String = "", // 通讯方式
    @ColumnInfo(name = "bind_ip")
    var bindIp: String = "", // 绑定设备ip
    @ColumnInfo(name = "delay_call_lift")
    var delayCallLift: String = "", // 延迟呼叫楼层
    @ColumnInfo(name = "install_floor_name")
    var installFloorName: String = "", // 安装楼层名称
    @ColumnInfo(name = "read_head_number")
    var readHeadNumber: String = "", // 读头机号
    @ColumnInfo(name = "install_position")
    var installPosition: String = "", // 安装楼层
    @ColumnInfo(name = "voice_reader")
    var voiceReader: String = "", // 语音读头号
    @ColumnInfo(name = "communication_ip")
    var communicationIp: String= "", // 后台通讯ip
    var token: String =""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
package com.ble.communicate.core

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import com.ble.commonlib.base.log
import com.ble.commonlib.core.utils.AppExecutors
import java.io.UnsupportedEncodingException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class BleManager(val context: Context, val callback: ()-> String) {
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothLeAdvertiser: BluetoothLeAdvertiser? = null
    private var mAdvertiseCallback: SampleAdvertiseCallback? = null
    private var mBluetoothGattServer: BluetoothGattServer? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val UUID_SERVER = UUID.fromString("")

    private val UUID_CHARWRITE = UUID.fromString("")
    private val devices: ArrayList<BluetoothDevice> = arrayListOf()
    var adverTimes = 0

    private val bluetoothGattServerCallback = object : BluetoothGattServerCallback() {
        // 1.工程app点击设备后会调用这里
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            // 连接状态发生改变，回调onCharacteristicChanged:device name->null, address->70:21:42:15:3D:64, status->0, newState->2
            log("连接状态发生改变，回调onCharacteristicChanged:device name->${device.name}, address->${device.address}, status->$status, newState->$newState")
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    val connect = mBluetoothGattServer?.connect(device, false)
                    log("主动连接->$connect")
                    devices.add(device)
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    devices.remove(device)
                }
            }
        }

        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
            log("客户端有读的请求,安卓回调该onCharacteristicReadRequest")
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, characteristic.value)
        }

        // 1.工程app点击设备后会调用这里
        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray
        ) {
            super.onCharacteristicWriteRequest(
                device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
            log("客户端有写的请求，安卓系统回调该onCharacteristicWriteRequest方法->${Arrays.toString(value)}")
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
            val str = String(value)
            log("字节转换->$str") // 字节转换->Open,00000577
            val split = str.split(",")
            if (split.size == 2) {
                if ("00000577" != split[1]) {
                    return
                }

                if (split[0] == "Open") {
                    try {
                        characteristic.value = callback.invoke().toByteArray(charset("UTF8")) // 通过蓝牙发送WIFI热点的名称跟密码给工程app
                        log("characteristic value->${characteristic.value.toString(Charsets.UTF_8)}") // characteristic value->ITL_5043,00031930
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                }

                mBluetoothGattServer?.notifyCharacteristicChanged(device, characteristic, false)
            }
        }

        // 特征被读取，当回复相应成功后，客户端会获取然后触发本方法
        override fun onDescriptorReadRequest(
            device: BluetoothDevice,
            requestId: Int,
            offset: Int,
            descriptor: BluetoothGattDescriptor
        ) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor)
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null)
        }

        override fun onDescriptorWriteRequest(
            device: BluetoothDevice,
            requestId: Int,
            descriptor: BluetoothGattDescriptor,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray
        ) {
            super.onDescriptorWriteRequest(
                device,
                requestId,
                descriptor,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService) {
            super.onServiceAdded(status, service)
            log("添加服务成功，安卓系统回调onServiceAdded方法,status->$status")
        }
    }

    init {
        mBluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager?.adapter
    }

    fun startAdvertising() {
        if (!mBluetoothAdapter?.isEnabled!!) {
            mBluetoothAdapter?.enable()
        }
        AppExecutors.schedule().schedule(stateAdvRunnable, 3000, TimeUnit.MILLISECONDS)
    }

    private val stateAdvRunnable = object : Runnable {
        override fun run() {
            if (!mBluetoothAdapter?.isEnabled!!) {
                log("蓝牙没打开")
                AppExecutors.schedule().schedule(this, 1000, TimeUnit.MILLISECONDS)
            } else {
                if (mAdvertiseCallback == null) {
                    mBluetoothManager?.adapter!!.name = "ITL_Pad_251"
                    val settings = buildAdvertiseSettings()
                    val data = buildAdvertiseData()
                    mAdvertiseCallback = SampleAdvertiseCallback()
                    val scanResponse = buildAdvertiseResponse("0003")
                    if (mBluetoothLeAdvertiser == null) {
                        mBluetoothLeAdvertiser = mBluetoothAdapter?.bluetoothLeAdvertiser
                    }
                    mBluetoothLeAdvertiser?.startAdvertising(settings, data, scanResponse, mAdvertiseCallback)
                }
            }
        }
    }

    fun unConn() {
        for (device in devices) {
            log("服务断开->${device.name}, $mBluetoothGattServer")
            mBluetoothGattServer?.cancelConnection(device)
        }
    }

    // 停止广播
    fun stopAdvertising() {
        AppExecutors.handler().removeCallbacks(stateAdvRunnable)
        mBluetoothLeAdvertiser?.run {
            stopAdvertising(mAdvertiseCallback)
            mAdvertiseCallback = null
        }
    }

    fun clear() {
        stopAdvertising()
        unConn()
        mBluetoothGattServer?.clearServices()
        mBluetoothGattServer?.close()
        mBluetoothGattServer = null
        mBluetoothAdapter?.disable()
    }

    private fun buildAdvertiseResponse(padId: String): AdvertiseData? {
        var utf8s = ByteArray(0)
        val objectId = "00000577"
        try {
            utf8s = "<$padId,$objectId>".toByteArray(charset("UTF8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        log("广播数据->$<$padId,$objectId>")
        return AdvertiseData.Builder()
            .addManufacturerData(2, utf8s)
            .build()
    }

    private fun buildAdvertiseSettings() : AdvertiseSettings {
        val settingsBuilder = AdvertiseSettings.Builder()
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
        settingsBuilder.setTimeout(0)
        settingsBuilder.setConnectable(true)
        return settingsBuilder.build()
    }

    private fun buildAdvertiseData(): AdvertiseData {
        val dataBuilder = AdvertiseData.Builder()
        dataBuilder.setIncludeDeviceName(true)
        return dataBuilder.build()
    }

    private inner class SampleAdvertiseCallback: AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            if (adverTimes > 5) {
                log("onStartFailure：蓝牙广播失败->$errorCode 重试次数->$adverTimes")
                return
            }
            adverTimes++
            log("onStartFailure：广播失败->$errorCode, 重试次数->$adverTimes")
            com.ble.communicate.service.BleMaintainService.stopService(context)
            log("onStartFailure：广播失败，关闭service")

            AppExecutors.handler().postDelayed({
                log("onStartFailure：广播失败,重新开启service")
                com.ble.communicate.service.BleMaintainService.startService(context)
            }, 3000)
        }

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            log("服务端的广播成功开启")
            log("BLE服务的广播启动成功：TxPowerLv= ${settingsInEffect.txPowerLevel}；mode=${settingsInEffect.mode}；timeout=${settingsInEffect.timeout}")
            AppExecutors.schedule().schedule(initRun, 1000, TimeUnit.MILLISECONDS)
        }
    }

    private val initRun = Runnable { initServices(context) }

    private fun initServices(context: Context) {
        if (mBluetoothGattServer == null) {
            mBluetoothGattServer = mBluetoothManager?.openGattServer(context, bluetoothGattServerCallback)
            val service = BluetoothGattService(UUID_SERVER, BluetoothGattService.SERVICE_TYPE_PRIMARY)
            val characteristicWrite = BluetoothGattCharacteristic(UUID_CHARWRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE)

            val descriptor = BluetoothGattDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"), BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE);
            characteristicWrite.addDescriptor(descriptor)
            service.addCharacteristic(characteristicWrite)
            mBluetoothGattServer?.addService(service)
            log("初始化服务成功, initService ok")
        }
    }
}
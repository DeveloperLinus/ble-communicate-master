package com.ble.commonlib.utils

import com.ble.commonlib.base.log
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import java.util.regex.Pattern

object NetUtils {
    private val IPV4_PATTERN = Pattern.compile(
        "^(" + "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
                "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$"
    )

    fun isIpv4Address(input: String?): Boolean {
        return IPV4_PATTERN.matcher(input).matches()
    }

    fun isIpReady(ip: String): Boolean {
        var enumeration: Enumeration<NetworkInterface>? = null
        try {
            enumeration = NetworkInterface.getNetworkInterfaces()
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                val nif = enumeration.nextElement()
                val inetAddresses =
                    nif.inetAddresses
                if (inetAddresses != null) {
                    while (inetAddresses.hasMoreElements()) {
                        val inetAddress = inetAddresses.nextElement()
                        if (!inetAddress.isLoopbackAddress && isIpv4Address(inetAddress.hostAddress)) {
                            log("存在ip=" + inetAddress.hostAddress)
                            if (inetAddress.hostAddress == ip) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    fun getIpAddress(useIpv4: Boolean): String {
        try {
            val nis =
                NetworkInterface.getNetworkInterfaces()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // 防止小米手机返回10.0.2.15
                if (!ni.isUp) {
                    continue
                }
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    val inetAddress = addresses.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        val hostAddress = inetAddress.hostAddress
                        val isIPv4 = hostAddress.indexOf(':') < 0
                        if (useIpv4) {
                            if (isIPv4) return hostAddress
                        } else {
                            if (!isIPv4) {
                                val index = hostAddress.indexOf('%')
                                return if (index < 0) hostAddress.toUpperCase() else hostAddress.substring(
                                    0,
                                    index
                                ).toUpperCase(Locale.ENGLISH)
                            }
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return "0.0.0.0"
    }
}
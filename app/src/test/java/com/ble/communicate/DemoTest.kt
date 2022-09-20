package com.ble.communicate

import org.junit.Test

import org.junit.Assert.*

class DemoTest {
    @Test
    fun byteEquals() {
        val b1: Byte = 0x00
        val b2: Byte = 0x00
        val b3 = b1 == b2
        println("b3->$b3")
    }
}
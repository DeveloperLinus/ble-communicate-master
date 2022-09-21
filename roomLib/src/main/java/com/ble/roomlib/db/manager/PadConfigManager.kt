package com.ble.roomlib.db.manager

import com.ble.roomlib.db.RoomRepository
import com.ble.roomlib.db.dao.PadConfigDao
import com.ble.roomlib.db.entity.PadConfigEntity

object PadConfigManager {
    private val dao: PadConfigDao by lazy { RoomRepository.getInstance().padConfigDao() }

    fun delete() {
        dao.delete()
    }

    fun insert(entity: PadConfigEntity) {
        dao.insert(entity)
    }

    fun getPadConfig(): PadConfigEntity? {
        return dao.query()
    }

    fun update(entity: PadConfigEntity): Int {
        return dao.update(entity)
    }
}
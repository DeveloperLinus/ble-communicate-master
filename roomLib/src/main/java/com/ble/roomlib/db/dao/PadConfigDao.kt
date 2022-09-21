package com.ble.roomlib.db.dao

import androidx.room.*
import com.ble.roomlib.db.entity.PadConfigEntity

@Dao
interface PadConfigDao {
    @Query("DELETE FROM padConfig")
    fun delete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: PadConfigEntity): Long

    @Query("SELECT * FROM padConfig LIMIT 1")
    fun query(): PadConfigEntity?

    @Update
    fun update(entity: PadConfigEntity): Int
}
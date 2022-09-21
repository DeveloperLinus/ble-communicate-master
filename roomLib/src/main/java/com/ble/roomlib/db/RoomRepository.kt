package com.ble.roomlib.db

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ble.roomlib.db.dao.PadConfigDao
import com.ble.roomlib.db.entity.PadConfigEntity
import com.ble.roomlib.db.utils.log
import java.io.File
import java.lang.NullPointerException

@Database(entities = [PadConfigEntity::class], version = 1, exportSchema = false)
abstract class RoomRepository : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: RoomRepository? = null
        private const val DATABASE_NAME = "BLE.db"

        @JvmStatic
        fun init(context: Context) {
            instance?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        fun getInstance() : RoomRepository {
            return instance ?: throw  NullPointerException("请先调用init初始化对象")
        }

        private fun buildDatabase(context: Context) :RoomRepository {
            return Room.databaseBuilder(context, RoomRepository::class.java, getDatabasePath())
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        log("数据库已经创建")
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        log("数据库已打开")
                    }
                })
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_1_2)
                .build()
        }

        private fun getDatabasePath(): String {
            val sdExist = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            log("getDatabasePath sdExist->$sdExist")
            return if (sdExist) {
                val dbDir = Environment.getExternalStorageDirectory().absolutePath + "/mnt/data"
                val dbPath = "$dbDir/$DATABASE_NAME"
                val dirFile = File(dbDir)
                if (!dirFile.exists()) dirFile.mkdirs()
                log("数据库路径:$dbPath")
                dbPath
            } else {
                DATABASE_NAME
            }
        }

        // 从版本1升级到版本2执行的操作
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("")
            }
        }
    }
    abstract fun padConfigDao(): PadConfigDao
}
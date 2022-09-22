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

@Database(entities = [PadConfigEntity::class], version = 3, exportSchema = false)
abstract class RoomRepository : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: RoomRepository? = null
        private const val DATABASE_NAME = "BLE.db"

        @JvmStatic
        fun init(context: Context) {
            log("开始初始化数据库对象->${instance == null}")
            instance?: synchronized(this) {
                log("开始构建数据...")
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
                .addMigrations(MIGRATION_2_3)
//                .fallbackToDestructiveMigration() // 开发阶段升级数据库直接抛弃数据
                .build()
        }

        private fun getDatabasePath(): String {
            val sdExist = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
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
                log("开始执行版本1到版本2升级操作")
                database.execSQL("ALTER TABLE padConfig ADD COLUMN plate_number TEXT")
                database.execSQL("ALTER TABLE padConfig ADD COLUMN communication_protocol TEXT")
                database.execSQL("ALTER TABLE padConfig ADD COLUMN bind_ip TEXT")
                database.execSQL("ALTER TABLE padConfig ADD COLUMN delay_call_lift TEXT")
                database.execSQL("ALTER TABLE padConfig ADD COLUMN install_floor_name TEXT")
                database.execSQL("ALTER TABLE padConfig ADD COLUMN read_head_number TEXT")
                database.execSQL("ALTER TABLE padConfig ADD COLUMN install_position TEXT")
                database.execSQL("ALTER TABLE padConfig ADD COLUMN voice_reader TEXT")
                log("执行版本1到版本2升级操作完毕")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                log("开始执行版本2到3升级")
                database.execSQL("ALTER TABLE padConfig ADD COLUMN floor_selection TEXT NOT NULL DEFAULT  \"1\"")
                log("版本2到3升级完毕")
            }
        }
    }
    abstract fun padConfigDao(): PadConfigDao
}
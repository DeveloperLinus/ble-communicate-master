package com.ble.communicate.ui

import android.content.Context
import android.content.Intent
import com.ble.commonlib.base.BaseBindingActivity
import com.ble.commonlib.base.log
import com.ble.communicate.R
import com.ble.communicate.databinding.ActivityEngineerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import java.util.*

class EngineerActivity : BaseBindingActivity<ActivityEngineerBinding>(), CoroutineScope by MainScope() {
    private lateinit var mPaw: String
    private lateinit var padID: String

    override fun getLayoutId(): Int {
        return R.layout.activity_engineer
    }

    override fun init() {
        mPaw = intent.getStringExtra(PASSWORD) ?: ""
        padID = intent.getStringExtra(NAME) ?: "ITL_PAD"
    }

    companion object {
        const val PASSWORD = "password"
        const val NAME = "NAME"
        const val IS_INIT = "IS_INIT"

        fun startActivity(context: Context) : String {
            // 随机生成热点的名称和密码
            val psw = String.format("%08d", Random().nextInt(9999999))
            val name = "ITL_${String.format("%04d", Random().nextInt(9999))}"
            log("psw:$psw, name:$name") // psw:00031930, name:ITL_5043
            val intent = Intent(context, EngineerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(PASSWORD, psw)
            intent.putExtra(NAME, name)
            intent.putExtra(IS_INIT, false)
            context.startActivity(intent)
            return "$name,$psw"
        }
    }
}
package com.example.bankmessage.modular.common.ui

import android.content.Intent
import android.content.IntentFilter
import com.base.library.mvvm.core.VMActivity
import com.blankj.utilcode.util.BusUtils
import com.blankj.utilcode.util.GsonUtils
import com.example.bankmessage.R
import com.example.bankmessage.base.AppConstant
import com.example.bankmessage.base.ChangeConstant
import com.example.bankmessage.broadcast.SmsReceiver
import com.example.bankmessage.entity.SystemJournal
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : VMActivity() {

    private var filter: IntentFilter? = null
    private var receiver: SmsReceiver? = null

    override fun initArgs(intent: Intent?) = null

    override fun initView() {
        super.initView()
        setContentView(R.layout.activity_test)
        BusUtils.register(this)
    }

    override fun initData() {
        filter = IntentFilter()
        filter?.addAction("android.provider.Telephony.SMS_RECEIVED")
        receiver = SmsReceiver()
        registerReceiver(receiver, filter) //注册广播接收器

        butStart.setOnClickListener {
            ChangeConstant.isMonitor = true
        }
        butEnd.setOnClickListener {
            ChangeConstant.isMonitor = false
        }
    }

    @BusUtils.Bus(tag = AppConstant.BUS_ReceivedSMS)
    fun receivedSMS(param: String) {
        val mSystemJournal = GsonUtils.fromJson(param, SystemJournal::class.java)
        // 先用收到短信的号码去匹配银行列表中的号码

    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let { unregisterReceiver(it) }
        BusUtils.unregister(this)
    }

}
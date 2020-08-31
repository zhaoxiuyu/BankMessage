package com.example.bankmessage.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import com.blankj.utilcode.util.BusUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.example.bankmessage.base.AppConstant
import com.example.bankmessage.base.ChangeConstant
import com.example.bankmessage.entity.SystemJournal
import com.example.bankmessage.utils.DataUtils

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, intent: Intent?) {
        // 短信监听打开的状态才读取短信内容
        if (ChangeConstant.isMonitor) {
            intent?.let {
                val content = StringBuilder() //用于存储短信内容
                val bundle = it.extras //通过getExtras()方法获取短信内容
                val format = it.getStringExtra("format")
                LogUtils.d("format $format")
                if (bundle != null) {
                    //根据pdus关键字获取短信字节数组，数组内的每个元素都是一条短信
                    val pdus = bundle["pdus"] as Array<Any>?
                    pdus?.let {
                        var mSmsMessage: SmsMessage? = null
                        for (item in pdus) {
                            //将字节数组转化为Message对象
                            mSmsMessage = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                SmsMessage.createFromPdu(item as ByteArray)
                            } else {
                                SmsMessage.createFromPdu(item as ByteArray, format)
                            }
                            val info =
                                "originatingAddress=${mSmsMessage?.originatingAddress} ; messageBody=${mSmsMessage?.messageBody}"
                            content.appendln(info) //获取短信内容
                        }
                        // 保存到日志数据库
                        DataUtils.saveSystemJournal(
                            "收到信息",
                            "${mSmsMessage?.messageBody}",
                            "${mSmsMessage?.originatingAddress}"
                        )
                        // 通知Fragment去提交短信
                        val params = SystemJournal()
                        params.tel = "${mSmsMessage?.originatingAddress}"
                        params.msg = "${mSmsMessage?.messageBody}"
                        BusUtils.post(AppConstant.BUS_ReceivedSMS, GsonUtils.toJson(params))
                    }
                }
                LogUtils.d(content.toString())
            }
        }
    }

}
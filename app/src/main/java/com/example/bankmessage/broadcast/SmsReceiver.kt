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
import com.example.bankmessage.modular.common.ui.Test
import com.example.bankmessage.utils.DataUtils

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, intent: Intent?) {
        // 短信监听打开的状态才读取短信内容
        if (ChangeConstant.isMonitor) {
//            val test = Test()
//            test.onReceive(p0, intent)

            intent?.let {
                val content = StringBuilder() //用于存储短信内容
                val sbMessageBody = StringBuilder()
                val sbOriginatingAddress = StringBuilder()

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

                            // 短信可能会被分割 多次发送,这里对短信内容进行累加
                            sbMessageBody.append(mSmsMessage?.messageBody)
                        }
                        sbOriginatingAddress.append(mSmsMessage?.originatingAddress)

                        // 保存到日志数据库
                        DataUtils.saveSystemJournal(
                            "收到信息", "$sbMessageBody", "$sbOriginatingAddress"
                        )
                        // 通知Fragment去提交短信
                        val params = SystemJournal()
                        params.tel = "$sbOriginatingAddress"
                        params.msg = "$sbMessageBody"
                        BusUtils.post(AppConstant.BUS_ReceivedSMS, GsonUtils.toJson(params))
                    }
                }
                content.appendln("发送人 : $sbOriginatingAddress")
                content.appendln("内容 : $sbMessageBody")
                LogUtils.d(content)
            }
        }
    }

}
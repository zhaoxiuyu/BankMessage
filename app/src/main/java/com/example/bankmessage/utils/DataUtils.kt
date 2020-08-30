package com.example.bankmessage.utils

import com.blankj.utilcode.util.TimeUtils
import com.example.bankmessage.entity.Bank
import com.example.bankmessage.entity.SystemJournal

object DataUtils {

    fun simulationData(): MutableList<String> {
        val datas = mutableListOf<String>()
        for (i in 1..14) {
            datas.add("$i")
        }
        return datas
    }

    fun getBankData(): MutableList<Bank> {
        val datas = mutableListOf<Bank>()
        for (i in 1..14) {
            datas.add(Bank())
        }
        return datas
    }

    // 保存系统日志  originatingAddress=+8618324488469 ; messageBody=123423756
    // 200 回调成功，2000 请求数据，其他失败显示红色
    fun saveSystemJournal(operation: String, msg: String, tel: String = "", httpCode: Int = 0) {
        val mSystemJournal = SystemJournal()
        mSystemJournal.time =
            TimeUtils.getNowString(TimeUtils.getSafeDateFormat("yyyy-MM-dd HH:mm:ss SSS"))
        mSystemJournal.operation = operation
        mSystemJournal.msg = msg
        mSystemJournal.tel = tel
        mSystemJournal.httpCode = httpCode
        mSystemJournal.save()
    }

}
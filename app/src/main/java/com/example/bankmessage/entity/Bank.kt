package com.example.bankmessage.entity

import com.base.library.util.MMKVUtils
import com.example.bankmessage.base.AppConstant
import org.litepal.crud.LitePalSupport

/**
 * 银行信息列表
 */
class Bank : LitePalSupport() {

    var username = MMKVUtils.getStr(AppConstant.username) // 用户名

    var code: String = ""

    var key: String = ""  // 设备 key
    var type: String = "" // 银行类型

    var today: Double = 0.0 // ": 555.33,  今日收款
    var yesterday: Double = 0.0  // ": 0.00  昨日收款

}
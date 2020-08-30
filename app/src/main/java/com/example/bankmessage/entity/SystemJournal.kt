package com.example.bankmessage.entity

import org.litepal.crud.LitePalSupport

/**
 * 系统日志
 */
class SystemJournal : LitePalSupport() {

    var operation: String = ""
    var msg: String = ""
    var time: String = ""

    // 请求状态，用来判断成功或者失败
    var httpCode: Int = 0

    // 短信发送人的手机号
    var tel: String = ""

}
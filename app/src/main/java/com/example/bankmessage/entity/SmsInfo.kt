package com.example.bankmessage.entity

/**
 * 短信
 */
class SmsInfo {
    var id = 0 //短信主键
    var address: String? = null //发送地址
    var type = 0 //类型
    var body: String? = null //短信内容
    var date: Long = 0 //时间

    override fun toString(): String {
        return "SmsInfo(id=$id, address=$address, type=$type, body=$body, date=$date)"
    }

}
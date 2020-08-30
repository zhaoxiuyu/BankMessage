package com.example.bankmessage.base

object AppConstant {

    /**
     * 接口地址
     */
    // 登录 / 用于登录
    const val Login = "App/Login"

    // 2.	校验设备KEY / 校验设备KEY是否存在
    const val CheckKey = "App/CheckKey"

    // 3.	获取设备信息 / 通过设备KEY获取设备信息
    const val GetDeviceInfo = "App/GetDeviceInfo"

    // 4.	获取银行列表 / 获取支持的银行列表
    const val GetBankList = "App/GetBankList"

    // 5.	提交短信信息 / 提交短信内容到服务器
    const val PostMessage = "App/PostMessage"

    // 6.	保持在线 / 保持当前登录在线状态，10秒请求一次
    const val KeepAlive = "App/KeepAlive"


    /**
     * 保存数据的key
     */

    // 登录时返回的
    const val userid = "userid"
    const val username = "username"

    /**
     * bus 通知
     */
    // 收到短信通知
    const val BUS_ReceivedSMS: String = "BUS_ReceivedSMS"

    // 刷新日志
    const val BUS_RefreshJournal: String = "BUS_RefreshJournal"

}
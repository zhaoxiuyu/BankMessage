package com.example.bankmessage.entity.response

class GetDeviceInfo {

    var BankName: String = "" // "": "中国银行", 银行名称
    var BankCode: String = "" // ": "BOC",  银行代码

    //  银行电话，正则表达格式，如平安银行支持106927995511和95511两个电话，则返回：(106927995511|95511)
    var BankTel: String = "" // ": "95566",

    var TodayAmount: Double = 0.0 // ": 555.33,  今日收款
    var YestodayAmount: Double = 0.0  // ": 0.00  昨日收款

    var DeviceKey: String = ""  // ": 0.00  设备 KEY

}
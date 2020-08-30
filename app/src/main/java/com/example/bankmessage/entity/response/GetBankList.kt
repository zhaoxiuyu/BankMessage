package com.example.bankmessage.entity.response

class GetBankList {

    var BankTel: String = "" // "": "(106927995511|95511)",  银行名称
    var BankCode: String = "" // ": "PAB",  银行代码

    // 银行电话，正则表达格式，如平安银行支持106927995511和95511两个电话，则返回：(106927995511|95511)
    var BankName: String = "" // ": "平安银行"

}
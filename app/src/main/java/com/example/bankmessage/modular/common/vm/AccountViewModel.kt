package com.example.bankmessage.modular.common.vm

import androidx.lifecycle.MutableLiveData
import com.base.library.entitys.BRequest
import com.base.library.entitys.BResponse
import com.base.library.mvvm.core.VMViewModel
import com.base.library.util.MMKVUtils
import com.example.bankmessage.base.AppConstant
import com.example.bankmessage.entity.response.GetBankList
import com.example.bankmessage.entity.response.GetDeviceInfo
import com.example.bankmessage.entity.response.Login
import com.example.bankmessage.utils.DataUtils

class AccountViewModel : VMViewModel() {

    /**
     * 校验设备key是否存在
     */
    val checkKeyLiveData by lazy { MutableLiveData<BResponse<String>>() }
    fun getCheckKey(devicekey: String) {
        val rxHttp = BRequest(AppConstant.CheckKey, BRequest.PostForm).apply {
            params =
                mapOf("userid" to MMKVUtils.getStr(AppConstant.userid), "devicekey" to devicekey)
        }.build()
        getData(rxHttp, checkKeyLiveData, String::class.java)
    }

    /**
     * 获取设备信息
     */
    val deviceInfoLiveData by lazy { MutableLiveData<BResponse<GetDeviceInfo>>() }
    fun GetDeviceInfo(devicekey: String) {
        val rxHttp = BRequest(AppConstant.GetDeviceInfo, BRequest.PostForm).apply {
            params =
                mapOf("userid" to MMKVUtils.getStr(AppConstant.userid), "devicekey" to devicekey)
        }.build()
        getData(rxHttp, deviceInfoLiveData, GetDeviceInfo::class.java)
    }

    /**
     * 获取银行列表
     */
    val bankListLiveData by lazy { MutableLiveData<BResponse<MutableList<GetBankList>>>() }
    fun GetBankList() {
        val rxHttp = BRequest(AppConstant.GetBankList, BRequest.PostForm).apply {
            silence = true
//            params = mapOf("userid" to userid)
        }.build()
        getDatas(rxHttp, bankListLiveData, GetBankList::class.java)
    }

    /**
     * 提交短信信息
     */
    val postMessageLiveData by lazy { MutableLiveData<BResponse<String>>() }
    fun PostMessage(devicekey: String, messge: String, telno: String) {
        DataUtils.saveSystemJournal("提交信息", "正在请求后台")

        val rxHttp = BRequest(AppConstant.PostMessage, BRequest.PostForm).apply {
            silence = true
            params = mapOf(
                "userid" to MMKVUtils.getStr(AppConstant.userid),
                "devicekey" to devicekey,
                "messge" to messge,
                "telno" to telno
            )
        }.build()
        getData(rxHttp, postMessageLiveData, String::class.java)
    }

    /**
     * 登录
     */
    val loginLiveData by lazy { MutableLiveData<BResponse<Login>>() }
    fun getLogin(username: String, pwd: String) {
        val rxHttp = BRequest(AppConstant.Login, BRequest.PostForm).apply {
            params = mapOf("username" to username, "pwd" to pwd)
        }.build()
        getData(rxHttp, loginLiveData, Login::class.java)
    }

    /**
     * 保持在线
     */
    val KeepAliveLiveData by lazy { MutableLiveData<BResponse<Login>>() }
    fun getKeepAlive(devicekey: String) {
        DataUtils.saveSystemJournal("保持在线", "正在请求后台")

        val rxHttp = BRequest(AppConstant.KeepAlive, BRequest.PostForm).apply {
            silence = true
            params =
                mapOf("username" to MMKVUtils.getStr(AppConstant.userid), "devicekey" to devicekey)
        }.build()
        getData(rxHttp, KeepAliveLiveData, Login::class.java)
    }

}
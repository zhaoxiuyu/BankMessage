package com.example.bankmessage.modular.common.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.library.interfaces.MyXPopupListener
import com.base.library.mvvm.core.VMFragment
import com.base.library.util.MMKVUtils
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.BusUtils.Bus
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.example.bankmessage.R
import com.example.bankmessage.base.AppConstant
import com.example.bankmessage.entity.Bank
import com.example.bankmessage.entity.SystemJournal
import com.example.bankmessage.entity.response.GetBankList
import com.example.bankmessage.modular.common.adapter.AccountAdapter
import com.example.bankmessage.modular.common.vm.AccountViewModel
import com.example.bankmessage.utils.DataUtils
import com.lxj.xpopup.XPopup
import com.rxjava.rxlife.life
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_account.*
import org.litepal.LitePal
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * 账号列表
 */
class AccountFragment : VMFragment(), OnItemChildClickListener {

    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(AccountViewModel::class.java) }

    private val mAdapter by lazy { AccountAdapter() }

    // 银行列表
    private val bankListData = mutableListOf<GetBankList>()

    companion object {
        fun newInstance() = AccountFragment()
    }

    override fun initArgs(bundle: Bundle?) = viewModel

    override fun initView(bundle: Bundle?) {
        setContentView(R.layout.fragment_account)
    }

    override fun initData() {
        BusUtils.register(this)
        initResponse()
        timing()
        polling()

        homeRv.layoutManager = LinearLayoutManager(requireActivity())
        homeRv.adapter = mAdapter
        mAdapter.setOnItemChildClickListener(this)
        mAdapter.setNewInstance(getBankAll())
        mAdapter.addChildClickViewIds(
            R.id.tvKey,
            R.id.ivQrCode,
            R.id.ivDelete
        )

        fab.setOnClickListener {
            val mBank = Bank()
            mBank.save()
            mAdapter.setNewInstance(getBankAll())
        }
    }

    var posBank: Bank? = null
    var posKey: String? = null
    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        posBank = mAdapter.getItem(position)
        when (view.id) {
            R.id.tvKey -> {
                XPopup.Builder(context).asInputConfirm(null, "请输入设备 KEY")
                { text ->
                    posKey = text
                    viewModel.getCheckKey(text)
                }.show()
            }
            R.id.ivQrCode -> {
                startPermissionCAMERA()
            }
            R.id.ivDelete -> {
                posBank?.delete()
                mAdapter.setNewInstance(getBankAll())
            }
        }
    }

    private fun initResponse() {
        // 校验设备key是否存在
        viewModel.checkKeyLiveData.observe(this, Observer {
            // 校验成功之后就获取设备信息
            posKey?.let {
                viewModel.GetDeviceInfo(it)
            }
        })
        // 获取设备信息
        viewModel.deviceInfoLiveData.observe(this, Observer {
            it.data?.let { info ->
                // 设备列表中，同一个银行类型只能添加一个,所以在这里判断如果有相同的就不添加
                var identical = false
                val bankAll = getBankAll()
                for (item in bankAll) {
                    if (info.BankName == item.type) {
                        identical = true
                        break
                    }
                }
                if (!identical) {
                    posBank?.key = posKey ?: ""
                    posBank?.type = info.BankName
                    posBank?.code = info.BankCode
                    posBank?.today = info.TodayAmount
                    posBank?.yesterday = info.YestodayAmount
                    posBank?.save()
                    mAdapter.setNewInstance(getBankAll())
                } else {
                    ToastUtils.showShort("不能添加相同的银行类型")
                }
            }
        })
        // 获取银行列表
        viewModel.bankListLiveData.observe(this, Observer {
            LogUtils.d("获取银行列表 更新 1")
            it.data?.let { getBankList ->
                bankListData.clear()
                bankListData.addAll(getBankList)
            }
        })
        // 提交短信信息
        viewModel.postMessageLiveData.observe(this, Observer {
            DataUtils.saveSystemJournal("提交返回", "${it.msg}", httpCode = it.code)
        })
        // 获取设备列表信息
        viewModel.deviceInfoListLiveData.observe(this, Observer {
            val bankAll = getBankAll()
            it.data?.forEach { info ->
                bankAll.forEach { bank ->
                    if (bank.key == info.DeviceKey
                        && bank.type == info.BankName
                        && bank.code == info.BankCode
                    ) {
                        bank.today = info.TodayAmount
                        bank.yesterday = info.YestodayAmount
                        bank.save()
                    }
                }
            }
            mAdapter.setNewInstance(getBankAll())
        })
    }

    @Bus(tag = AppConstant.BUS_RefreshDeviceInfoList)
    fun refreshDeviceInfoList() {
        LogUtils.d("手动刷新设备列表信息")
        getDeviceInfoList()
    }

    @Bus(tag = AppConstant.BUS_ReceivedSMS)
    fun receivedSMS(param: String) {
        val mSystemJournal = GsonUtils.fromJson(param, SystemJournal::class.java)
        // 先用收到短信的号码去匹配银行列表中的号码
        var BankCode = ""
        for (bank in bankListData) {
            val regex = bank.BankTel // 银行列表中的电话号码
            val str = mSystemJournal.tel // 短信收到的号码

            val p = Pattern.compile(regex)
            val m = p.matcher(str)

            if (m.matches()) {
                BankCode = bank.BankCode
            }
        }
        // 再通过银行代码去匹配设备列表中的设备
        if (!StringUtils.isEmpty(BankCode)) {
            val bankDatas = getBankAll()
            var newData: Bank? = null
            for (data in bankDatas) {
                if (data.code == BankCode) {
                    newData = data
                }
            }
            if (newData != null) {
                viewModel.PostMessage(newData.key, mSystemJournal.msg, mSystemJournal.tel)
            } else {
                DataUtils.saveSystemJournal("提示", "该号码无法匹配任何设备", httpCode = 404)
            }
        } else {
            DataUtils.saveSystemJournal("提示", "该号码无法匹配银行列表", httpCode = 404)
        }
    }

    override fun onError(msg: String, url: String?, isFinish: Boolean, isSilence: Boolean) {
        super.onError(msg, url, isFinish, isSilence)
        if (url == AppConstant.PostMessage) {
            DataUtils.saveSystemJournal("提交返回", "$msg", httpCode = 500)
        }
    }

    // 银行列表，先默认获取一次，然后再每五分钟调用一次
    private fun timing() {
        viewModel.GetBankList()
        Observable.timer(5, TimeUnit.MINUTES)
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .life(this)
            .subscribe {
                viewModel.GetBankList()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            val result = it.getStringExtra("qrcode")
            if (!StringUtils.isEmpty(result)) {
                posKey = result
                viewModel.getCheckKey(result ?: "")
            }
        }
    }

    // 获取设备列表数据
    private fun getDeviceInfoList() {
        val sb = StringBuilder()
        val bankAll = getBankAll()
        bankAll.forEach {
            if (!StringUtils.isEmpty(it.key)) {
                sb.append("${it.key},")
            }
        }
        viewModel.GetDeviceInfoList(sb.toString())
    }

    // 获取设备列表信息
    private var mDisposable: Disposable? = null
    private fun polling() {
        Observable.interval(60, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.rxjava3.core.Observer<Long?> {
                override fun onSubscribe(d: @NonNull Disposable?) {
                    mDisposable = d
                }

                override fun onNext(aLong: @NonNull Long?) {
                    getDeviceInfoList()
                }

                override fun onError(e: @NonNull Throwable?) {
                    e?.printStackTrace()
                }

                override fun onComplete() {}
            })
    }

    // 拍照权限
    private fun startPermissionCAMERA() {
        AndPermission.with(this).runtime()
            .permission(Permission.CAMERA)
            .onGranted {
                startActivityForResult(Intent(requireActivity(), QrCodeActivity::class.java), 1)
            }.rationale { context, data, executor ->
                showDialog(content = "需要权限才能继续执行", confirmLi = object : MyXPopupListener {
                    override fun onDis() {
                        dismissDialog()
                        executor.execute()
                    }
                }, cancelLi = object : MyXPopupListener {
                    override fun onDis() {
                        dismissDialog()
                        executor.cancel()
                    }
                }, isHideCancel = false)
            }.onDenied {
                showDialog(content = "手动设置权限", confirmLi = object : MyXPopupListener {
                    override fun onDis() {
                        dismissDialog()
                        AndPermission.with(requireActivity()).runtime().setting().start(1)
                    }
                }, cancelLi = object : MyXPopupListener {
                    override fun onDis() {
                        dismissDialog()
                    }
                }, isHideCancel = false)
            }.start()
    }

    private fun getBankAll(): MutableList<Bank> {
        return LitePal.where("username = ?", MMKVUtils.getStr(AppConstant.username))
            .find(Bank::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        BusUtils.unregister(this)
    }

}
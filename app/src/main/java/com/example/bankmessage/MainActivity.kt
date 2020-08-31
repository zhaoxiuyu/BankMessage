package com.example.bankmessage

import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.base.library.interfaces.MyXPopupListener
import com.base.library.mvvm.core.VMActivity
import com.base.library.util.MMKVUtils
import com.blankj.utilcode.util.*
import com.example.bankmessage.base.AppConstant
import com.example.bankmessage.base.ChangeConstant
import com.example.bankmessage.broadcast.SmsReceiver
import com.example.bankmessage.entity.Bank
import com.example.bankmessage.entity.SystemJournal
import com.example.bankmessage.modular.common.ui.AccountFragment
import com.example.bankmessage.modular.common.ui.JournalFragment
import com.example.bankmessage.modular.common.ui.OtherFragment
import com.example.bankmessage.modular.common.vm.AccountViewModel
import com.example.bankmessage.utils.DataUtils
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.litepal.LitePal
import java.util.concurrent.TimeUnit


class MainActivity : VMActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(AccountViewModel::class.java) }

    private val mAccountFragment by lazy { AccountFragment.newInstance() }
    private val mJournalFragment by lazy { JournalFragment.newInstance() }
    private val mOtherFragment by lazy { OtherFragment.newInstance() }

    private var filter: IntentFilter? = null
    private var receiver: SmsReceiver? = null

    override fun initArgs(intent: Intent?) = viewModel

    override fun initView() {
        super.initView()
        setContentView(R.layout.activity_main)

        filter = IntentFilter()
        filter?.addAction("android.provider.Telephony.SMS_RECEIVED")
        receiver = SmsReceiver()
        registerReceiver(receiver, filter) //注册广播接收器
    }

    override fun initData() {
        updateUserInfo(false)
        initResponse()

        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> onTabSelected(0)
                R.id.navigation_dashboard -> onTabSelected(1)
                R.id.navigation_notifications -> onTabSelected(2)
            }
            true
        }
        navView.selectedItemId = R.id.navigation_home
        tvRefresh.visibility = View.VISIBLE
        tvclear.visibility = View.GONE

        tvStart.setOnClickListener {
            startPermission()
        }
        tvclear.setOnClickListener {
            // 把保持在线和保持返回清理掉，剩下的留下，如果保持返回有网络异常也留下 LitePal.deleteAll(MainEntity.class,"name = ?" ,"张三");
            LitePal.deleteAll(SystemJournal::class.java, "operation = ?", "保持在线")
            LitePal.deleteAll(
                SystemJournal::class.java,
                "operation = ? and httpCode = ?",
                "保持返回",
                "0"
            )
            BusUtils.post(AppConstant.BUS_RefreshJournal)
            ToastUtils.showLong("清理成功")
        }
        tvRefresh.setOnClickListener {
            BusUtils.post(AppConstant.BUS_RefreshDeviceInfoList)
        }
    }

    private fun initResponse() {
        // 保持在线
        viewModel.KeepAliveLiveData.observe(this, Observer {
            if (it.code == 200) {
                DataUtils.saveSystemJournal("保持返回", "${it.msg}")
            } else {
                DataUtils.saveSystemJournal("保持返回", "${it.msg}", httpCode = it.code)
            }
            BusUtils.post(AppConstant.BUS_RefreshJournal)
        })
    }

    override fun onError(msg: String, url: String?, isFinish: Boolean, isSilence: Boolean) {
        if (url == AppConstant.KeepAlive) {
            DataUtils.saveSystemJournal("保持返回", "$msg", httpCode = 500)
            BusUtils.post(AppConstant.BUS_RefreshJournal)
        }
        super.onError(msg, url, isFinish, isSilence)
    }

    private fun updateUserInfo(isStart: Boolean = false) {
        if (isStart) {
            SpanUtils.with(tvUserInfo)
                .append("当前用户：${MMKVUtils.getStr(AppConstant.username)}")
                .appendLine()
                .append("运行状态：")
                .append("已启动").setForegroundColor(ColorUtils.getColor(R.color.color_34CC2C))
                .create()
        } else {
            SpanUtils.with(tvUserInfo)
                .append("当前用户：${MMKVUtils.getStr(AppConstant.username)}")
                .appendLine()
                .append("运行状态：")
                .append("未启动").setForegroundColor(ColorUtils.getColor(R.color.color_E70012))
                .create()
        }
    }

    // 短信权限
    private fun startPermission() {
        AndPermission.with(this).runtime()
            .permission(Permission.RECEIVE_SMS)
            .onGranted {
                SMSRegister()
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
                        AndPermission.with(this@MainActivity).runtime().setting().start(1)
                    }
                }, cancelLi = object : MyXPopupListener {
                    override fun onDis() {
                        dismissDialog()
                    }
                }, isHideCancel = false)
            }.start()
    }

    private fun SMSRegister() {
        if (ChangeConstant.isMonitor) {
            // 如果监听打开，就关闭
            ChangeConstant.isMonitor = false

            tvStart.text = "启动"
            updateUserInfo(false)

            mDisposable?.dispose()
            LogUtils.d("结束监听")
        } else {
            // 如果监听关闭，就打开
            ChangeConstant.isMonitor = true
            LogUtils.d("开始监听")

            polling()
            tvStart.text = "停止"
            updateUserInfo(true)
        }
        LogUtils.d("监听是否打开 : ${ChangeConstant.isMonitor}")
    }

    // 保持在线
    private var mDisposable: Disposable? = null
    private fun polling() {
        Observable.interval(15, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.rxjava3.core.Observer<Long?> {
                override fun onSubscribe(d: @NonNull Disposable?) {
                    mDisposable = d
                }

                override fun onNext(aLong: @NonNull Long?) {
                    val sb = StringBuilder()
                    val bankAll = getBankAll()
                    bankAll.forEach {
                        if (!StringUtils.isEmpty(it.key)) {
                            sb.append("${it.key},")
                        }
                    }
                    viewModel.getKeepAlive(sb.toString())
                }

                override fun onError(e: @NonNull Throwable?) {
                    e?.printStackTrace()
                }

                override fun onComplete() {}
            })
    }

    private fun getBankAll(): MutableList<Bank> {
        return LitePal.where("username = ?", MMKVUtils.getStr(AppConstant.username))
            .find(Bank::class.java)
    }

    // 统计-工作台-消息 暂未开放，所以显示同一个 Fragment 给出相同的提示即可。
    private fun onTabSelected(position: Int) {
        when (position) {
            0 -> {
                tvRefresh.visibility = View.VISIBLE
                tvclear.visibility = View.GONE
                showFragment(mAccountFragment, "mAccountFragment")
            }
            1 -> {
                tvRefresh.visibility = View.GONE
                tvclear.visibility = View.VISIBLE
                showFragment(mJournalFragment, "mJournalFragment")
            }
            2 -> {
                tvRefresh.visibility = View.GONE
                tvclear.visibility = View.GONE
                showFragment(mOtherFragment, "mOtherFragment")
            }
        }
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        val findFragment = FragmentUtils.findFragment(supportFragmentManager, tag)
        FragmentUtils.hide(supportFragmentManager)
        if (findFragment != null) {
            FragmentUtils.show(findFragment)
        } else {
            FragmentUtils.add(supportFragmentManager, fragment, R.id.flContent, tag)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("Activity onDestroy")
        receiver?.let { unregisterReceiver(it) }
    }

}
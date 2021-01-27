package com.example.bankmessage.modular.common.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.base.library.interfaces.MyXPopupListener
import com.base.library.mvvm.core.VMActivity
import com.base.library.util.MMKVUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.bankmessage.MainActivity
import com.example.bankmessage.R
import com.example.bankmessage.base.AppConstant
import com.example.bankmessage.modular.common.vm.AccountViewModel
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_login.*
import org.litepal.LitePal

class LoginActivity : VMActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(AccountViewModel::class.java) }

    override fun initArgs(intent: Intent?) = viewModel

    override fun initView() {

        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            LogUtils.d()
            finish()
            return
        }

        super.initView()
        setContentView(R.layout.activity_login)
    }

    override fun initData() {
        initResponse()
        mbLogin.setOnClickListener {
            val tieUserName = tieUserName.text.toString()
            val tiePassWord = tiePassWord.text.toString()
            if (StringUtils.isEmpty(tieUserName) || StringUtils.isEmpty(tiePassWord)) {
                ToastUtils.showLong("用户名密码不能为空")
                return@setOnClickListener
            }
            startPermission()
        }
        toTest.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
        ignoreBatteryOptimization()
    }

    /**
     * 忽略电池优化
     */
    private fun ignoreBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager =
                getSystemService(Context.POWER_SERVICE) as PowerManager
            val hasIgnored =
                powerManager.isIgnoringBatteryOptimizations(this.packageName)
            //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
            if (!hasIgnored) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:" + this.packageName)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            } else {
                Log.d("ignoreBattery", "hasIgnored")
            }
        }
    }

    private fun initResponse() {
        viewModel.loginLiveData.observe(this, Observer {
            it.data?.let { login ->
                MMKVUtils.put(AppConstant.userid, login.id)
                MMKVUtils.put(AppConstant.username, login.username)

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
    }

    private fun startPermission() {
        AndPermission.with(this).runtime()
            .permission(Permission.Group.STORAGE)
            .onGranted {
                LitePal.initialize(this)
                viewModel.getLogin(tieUserName.text.toString(), tiePassWord.text.toString())
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
                        AndPermission.with(this@LoginActivity).runtime().setting().start(1)
                    }
                }, cancelLi = object : MyXPopupListener {
                    override fun onDis() {
                        dismissDialog()
                    }
                }, isHideCancel = false)
            }.start()
    }

}
package com.example.bankmessage.modular.common.ui

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.base.library.interfaces.MyXPopupListener
import com.base.library.mvvm.core.VMActivity
import com.base.library.util.MMKVUtils
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

//        tieUserName.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                LogUtils.d(p0?.toString())
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//        })
//        val filter = InputFilter { source, start, end, dest, dstart, dend ->
//            val regEx =
//                "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
//            val pattern = Pattern.compile(regEx)
//            val matcher = pattern.matcher(source.toString())
//            if (matcher.find() || source == " ") "" else null
//        }
//        val lengthFilter = LengthFilter(10)
//        tieUserName.filters = arrayOf(filter, lengthFilter)
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
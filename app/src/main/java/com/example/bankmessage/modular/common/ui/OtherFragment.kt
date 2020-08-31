package com.example.bankmessage.modular.common.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.library.mvvm.core.VMFragment
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.example.bankmessage.R
import com.example.bankmessage.entity.SmsInfo
import com.example.bankmessage.modular.common.adapter.OtherAdapter
import com.example.bankmessage.modular.common.vm.AccountViewModel
import kotlinx.android.synthetic.main.fragment_other.*

/**
 * 其他
 */
class OtherFragment : VMFragment(), OnItemChildClickListener {

    private val projection = arrayOf("_id", "address", "type", "body", "date")
    private val SMS_ALL = "content://sms/" // 所有[短信]
    private val SMS_INBOX = "content://sms/inbox" // 收件箱
    private val SMS_SEND = "content://sms/sent" // 	已发送
    private val SMS_DRAFT = "content://sms/draft" // 草稿
    private val SMS_OUTBOX = "content://sms/outbox" // 发件箱
    private val SMS_FAILED = "content://sms/failed" // 发送失败
    private val SMS_QUEUED = "content://sms/queued" // 待发送列表

    private val smsDatas = mutableListOf<SmsInfo>()

    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(AccountViewModel::class.java) }

    private val mAdapter by lazy { OtherAdapter() }

    companion object {
        fun newInstance() = OtherFragment()
    }

    override fun initArgs(bundle: Bundle?) = viewModel

    override fun initView(bundle: Bundle?) {
        super.initView(bundle)
        setContentView(R.layout.fragment_other)
    }

    override fun initData() {
        initResponse()

        rvOther.layoutManager = LinearLayoutManager(requireActivity())
        rvOther.adapter = mAdapter
        mAdapter.setOnItemChildClickListener(this)

        but2.setOnClickListener {
            viewModel.GetBankList()
        }
    }

    private fun initResponse() {
        // 获取银行列表
        viewModel.bankListLiveData.observe(this, Observer {
            LogUtils.d("获取银行列表 更新 2")
            mAdapter.setNewInstance(it.data)
        })
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
    }

}
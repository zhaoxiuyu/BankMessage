package com.example.bankmessage.modular.common.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.library.mvvm.core.VMFragment
import com.base.library.util.MMKVUtils
import com.blankj.utilcode.util.BusUtils
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.example.bankmessage.R
import com.example.bankmessage.base.AppConstant
import com.example.bankmessage.entity.Bank
import com.example.bankmessage.entity.SystemJournal
import com.example.bankmessage.modular.common.adapter.JournalAdapter
import com.example.bankmessage.modular.common.vm.AccountViewModel
import com.example.bankmessage.utils.DataUtils
import kotlinx.android.synthetic.main.fragment_journal.*
import org.litepal.LitePal

/**
 * 系统日志
 */
class JournalFragment : VMFragment(), OnItemChildClickListener {

    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(AccountViewModel::class.java) }

    private val mAdapter by lazy { JournalAdapter() }

    companion object {
        fun newInstance() = JournalFragment()
    }

    override fun initArgs(bundle: Bundle?) = viewModel

    override fun initView(bundle: Bundle?) {
        super.initView(bundle)
        setContentView(R.layout.fragment_journal)
    }

    override fun initData() {
        BusUtils.register(this)

        journalRv.layoutManager = LinearLayoutManager(requireActivity())
        journalRv.adapter = mAdapter
        mAdapter.animationEnable = true
        mAdapter.setOnItemChildClickListener(this)
        mAdapter.setNewInstance(
            LitePal.order("time desc").limit(2000).find(SystemJournal::class.java)
        )
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mAdapter.setNewInstance(
                LitePal.order("time desc").limit(2000).find(SystemJournal::class.java)
            )
        }
    }

    @BusUtils.Bus(tag = AppConstant.BUS_RefreshJournal)
    fun receivedSMS() {
        LogUtils.d("收到通知 刷新一次日志")
        mAdapter.setNewInstance(
            LitePal.order("time desc").limit(2000).find(SystemJournal::class.java)
        )
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        BusUtils.unregister(this)
    }
}

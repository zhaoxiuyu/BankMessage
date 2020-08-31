package com.example.bankmessage.modular.common.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.library.mvvm.core.VMFragment
import com.blankj.utilcode.util.BusUtils
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.example.bankmessage.R
import com.example.bankmessage.base.AppConstant
import com.example.bankmessage.entity.SystemJournal
import com.example.bankmessage.modular.common.adapter.JournalAdapter
import com.example.bankmessage.modular.common.vm.AccountViewModel
import kotlinx.android.synthetic.main.fragment_journal.*
import org.litepal.LitePal

/**
 * 系统日志
 */
class JournalFragment : VMFragment(), RecyclerView.OnItemTouchListener {

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
        journalRv.addOnItemTouchListener(this)
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
        LogUtils.d("收到通知 刷新一次日志,是否可以刷新页面")
        mAdapter.setNewInstance(
            LitePal.order("time desc").limit(2000).find(SystemJournal::class.java)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        BusUtils.unregister(this)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("onInterceptTouchEvent", "按下事件")
                timer.cancel()
            }
            MotionEvent.ACTION_UP -> {
                Log.d("onInterceptTouchEvent", "停止操作1")
                timer.start()
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d("onInterceptTouchEvent", "停止操作2")
                timer.start()
            }
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }

    private val timer = object : CountDownTimer(20000, 1000) {
        override fun onFinish() {
            LogUtils.d("倒计时结束")
            journalRv.smoothScrollToPosition(0)
        }

        override fun onTick(p0: Long) {
            Log.d("倒计时 :", "$p0")
        }
    }

}

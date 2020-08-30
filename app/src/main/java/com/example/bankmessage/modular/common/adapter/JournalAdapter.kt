package com.example.bankmessage.modular.common.adapter

import android.widget.TextView
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.bankmessage.R
import com.example.bankmessage.entity.SystemJournal

class JournalAdapter : BaseQuickAdapter<SystemJournal, BaseViewHolder>(R.layout.item_journal) {

    override fun convert(holder: BaseViewHolder, item: SystemJournal) {
        val tvJournal = holder.getView<TextView>(R.id.tvJournal)
        when (item.httpCode) {
            200 -> {
                SpanUtils.with(tvJournal)
                    .append("[${item.time}]")
                    .setForegroundColor(ColorUtils.getColor(R.color.color_333333))
                    .appendLine()
                    .append("[${item.operation}]${item.msg}")
                    .setForegroundColor(ColorUtils.getColor(R.color.color_34CC2C))
                    .create()
            }
            0 -> {
                SpanUtils.with(tvJournal)
                    .append("[${item.time}]")
                    .setForegroundColor(ColorUtils.getColor(R.color.color_333333))
                    .appendLine()
                    .append("[${item.operation}]${item.msg}")
                    .setForegroundColor(ColorUtils.getColor(R.color.color_333333))
                    .create()
            }
            else -> {
                SpanUtils.with(tvJournal)
                    .append("[${item.time}]")
                    .setForegroundColor(ColorUtils.getColor(R.color.color_333333))
                    .appendLine()
                    .append("[${item.operation}]${item.msg}")
                    .setForegroundColor(ColorUtils.getColor(R.color.color_E70012))
                    .create()
            }
        }
    }

}
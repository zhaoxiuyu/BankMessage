package com.example.bankmessage.modular.common.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.bankmessage.R
import com.example.bankmessage.entity.Bank

class AccountAdapter : BaseQuickAdapter<Bank, BaseViewHolder>(R.layout.item_account) {

    override fun convert(holder: BaseViewHolder, item: Bank) {
        holder.setText(R.id.tvKey, item.key)
        holder.setText(R.id.tvType, "银行类型 : ${item.type}")
        holder.setText(R.id.tvToday, "今日收款 : ${item.today}")
        holder.setText(R.id.tvYesterday, "昨日收款 : ${item.yesterday}")
    }

}
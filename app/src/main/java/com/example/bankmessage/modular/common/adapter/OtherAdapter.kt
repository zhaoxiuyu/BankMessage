package com.example.bankmessage.modular.common.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.bankmessage.R
import com.example.bankmessage.entity.response.GetBankList

class OtherAdapter : BaseQuickAdapter<GetBankList, BaseViewHolder>(R.layout.item_other) {

    override fun convert(holder: BaseViewHolder, item: GetBankList) {
        holder.setText(R.id.tvInfo, "${item.BankCode}\n${item.BankName}\n${item.BankTel}")
    }

}
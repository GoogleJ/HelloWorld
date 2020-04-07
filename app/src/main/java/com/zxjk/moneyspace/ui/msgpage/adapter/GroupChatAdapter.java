package com.zxjk.moneyspace.ui.msgpage.adapter;

import android.annotation.SuppressLint;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.GroupChatResponse;
import com.zxjk.moneyspace.utils.ImageUtil;

public class GroupChatAdapter extends BaseQuickAdapter<GroupChatResponse, BaseViewHolder> {

    public GroupChatAdapter() {
        super(R.layout.item_group_chat);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void convert(BaseViewHolder helper, GroupChatResponse item) {

        helper.setText(R.id.group_name, item.getGroupNikeName())
                .setText(R.id.group_message, item.getGroupSign())
                .addOnClickListener(R.id.m_group_chat);

        ImageUtil.loadGroupPortrait(helper.getView(R.id.group_chat_iamge), item.getHeadPortrait(), 56, 2);
    }
}
